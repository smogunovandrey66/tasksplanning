package com.smogunovandrey.tasksplanning.runtask

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.db.*
import com.smogunovandrey.tasksplanning.taskstemplate.RunPoint
import com.smogunovandrey.tasksplanning.taskstemplate.RunTask
import com.smogunovandrey.tasksplanning.taskstemplate.RunTaskWithPoints
import com.smogunovandrey.tasksplanning.utils.toRunPointDB
import com.smogunovandrey.tasksplanning.utils.toRunTaskDB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds

class ManagerActiveTask private constructor(val context: Context) {
    private val dao by lazy {
        AppDatabase.getInstance(context).mainDao()
    }

    private val _activeRunTaskWithPointsFlow = MutableStateFlow<RunTaskWithPoints?>(null)
    val activeRunTaskWithPointsFlow: StateFlow<RunTaskWithPoints?> = _activeRunTaskWithPointsFlow

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun notificationBuilder(commandId: Int) = NotificationCompat.Builder(context, CHANNEL_ID)
        .setCustomContentView(
            RemoteViews(context.packageName, R.layout.notification_run_task)
                .apply {
                    val intentNext = Intent(context, RunBroadcastReceiver::class.java)
                    intentNext.putExtra(COMMAND_ID, commandId)

                    val pendingIntentNext = PendingIntent.getBroadcast(context, 1,
                    intentNext, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

                    setOnClickPendingIntent(R.id.btn_next, pendingIntentNext)
                    _activeRunTaskWithPointsFlow.value?.let {runTaskWithPoints ->
                        setTextViewText(R.id.txt_name, runTaskWithPoints.runTask.name)

                        val curPoint = runTaskWithPoints.curPoint()
                        if(curPoint == null)
                            setImageViewResource(R.id.btn_next, R.drawable.baseline_play_arrow_24)
                        else
                            setTextViewText(R.id.txt_number, curPoint.num.toString())
                    }

                    val intentCancel = Intent(context, RunBroadcastReceiver::class.java)
                    intentCancel.putExtra(COMMAND_ID, COMMAND_CANCEL)

                    val pendingIntentCancel = PendingIntent.getBroadcast(context, 1,
                    intentCancel, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

                    setOnClickPendingIntent(R.id.btn_cancel, pendingIntentCancel)
                }
        )
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setSmallIcon(R.drawable.baseline_run_circle_24)




    suspend fun reloadActiviTask() {
        val activeRunTaskWithPointDB = dao.activeRunTaskWithPointsSuspend()
        var activeRunTaskWithPoints: RunTaskWithPoints? = null
        activeRunTaskWithPointDB?.let {
            activeRunTaskWithPoints = getRunTask(activeRunTaskWithPointDB.runTask.idRunTask)
        }
        _activeRunTaskWithPointsFlow.emit(activeRunTaskWithPoints)
    }

    suspend fun getRunTask(idRunTask: Long): RunTaskWithPoints {
        Log.d("TasksTemplateFragment", "getRunTask idRunTask=$idRunTask")
        val runTaskWithPointsDB = dao.runTaskWithPointsByIdSuspend(idRunTask)
        Log.d("TasksTemplateFragment", "getRunTask runTaskWithPointsDB=$runTaskWithPointsDB")

        val runTaskDB = runTaskWithPointsDB.runTask
        val taskDB = dao.taskByIdSuspend(runTaskDB.idTask)
        val runTaskWithPoints = RunTaskWithPoints(
            RunTask(
                runTaskDB.idRunTask,
                runTaskDB.idTask,
                taskDB.name,
                runTaskDB.dateCreate,
                runTaskDB.active
            ),
            runTaskWithPointsDB.runPoints.map {
                var dateMark = 0L
                it.dateMark?.let { date ->
                    //First point
                    if(it.num == 1)
                        dateMark = date.time - runTaskDB.dateCreate.time
                    else {
                        //Other points
                        val prevDate = runTaskWithPointsDB.runPoints[it.num - 2].dateMark
                        if(prevDate != null)
                            dateMark = date.time - prevDate.time
                    }
                }

                val pointDB = dao.pointByIdSuspend(it.idPoint)

                RunPoint(
                    it.idRunPoint,
                    taskDB.idTask,
                    it.idPoint,
                    it.num,
                    pointDB.name,
                    pointDB.triggerType,
                    dateMark,
                    it.dateMark
                )
            }.toMutableList()
        )

        return runTaskWithPoints
    }

    suspend fun startTask(idTask: Long) {
        //1 Insert in DB
        val taskWithPointsDB: TaskWithPointDB = dao.taskWithPointsSuspend(idTask)
        val idRunTask = dao.insertRunTask(RunTaskDB(0, idTask))
        val points = taskWithPointsDB.points.sortedWith { p1, p2 ->
            p1.num - p2.num
        }
        for (pointDB in points) {
            dao.insertRunPoint(
                RunPointDB(
                    0,
                    idRunTask,
                    pointDB.idPoint,
                    pointDB.num
                )
            )
        }
        reloadActiviTask()

        Log.d("RunTaskViewFragment", "in startTask 2 ${_activeRunTaskWithPointsFlow.value}")
        Log.d("RunTaskViewFragment", "in startTask 3 ${activeRunTaskWithPointsFlow.value}")

        //2 Start Foreground Service
        ContextCompat.startForegroundService(context,
            Intent(context, RunService::class.java).apply {
                putExtra(COMMAND_ID, COMMAND_START)
            }
        )
    }

    suspend fun markPoint() { //or name as 'nextPoint'
        //Update DB
        val runTaskWithPoints = _activeRunTaskWithPointsFlow.value
        runTaskWithPoints?.let {
            var curIdx = 0
            val lastIdx = it.points.size - 1
            for (point in it.points) {
                if (point.dateMark == null) {
                    point.dateMark = Date()
                    val idRunTask = runTaskWithPoints.runTask.idRunTask
                    dao.updateRunPoint(point.toRunPointDB(idRunTask))

                    //Set active to false for RunTask if last point
                    if (curIdx == lastIdx) {
                        //Deactivate task
                        runTaskWithPoints.runTask.active = false
                        dao.updateRunTask(runTaskWithPoints.runTask.toRunTaskDB())

                        //Stop service
                        context.stopService(Intent(context, RunService::class.java))
                    }

                    _activeRunTaskWithPointsFlow.emit(getRunTask(idRunTask))

                    //Update notification
                    if(curIdx < lastIdx)
                        notificationManager.notify(NOTIFICATION_ID, notificationBuilder(COMMAND_NEXT).build())

                    break
                }
                curIdx++
            }

            reloadActiviTask()
        }
    }

    suspend fun cancelTask() {
        val runTaskWithPoints = _activeRunTaskWithPointsFlow.value
        Log.d("TasksTemplateFragment", "cancel task $runTaskWithPoints")
        //Delete from DB
        runTaskWithPoints?.let {
            for (point in it.points) {
                val idRunTask = runTaskWithPoints.runTask.idRunTask
                val delRunPointDB = point.toRunPointDB(idRunTask)
                Log.d("TasksTemplateFragment", "delRunPointDB=$delRunPointDB")
                val delId = dao.deleteRunPoint(delRunPointDB)
                Log.d("TasksTemplateFragment", "delId=$delId")
            }

            val delRunTaskDB = runTaskWithPoints.runTask.toRunTaskDB()
            Log.d("TasksTemplateFragment", "delRunTaskDB=$delRunTaskDB")
            dao.deleteRunTask(delRunTaskDB)
            //Stop service
            context.stopService(Intent(context, RunService::class.java))

            _activeRunTaskWithPointsFlow.value = null
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1

        const val CHANNEL_ID = "channel id for run task"
        const val CHANNEL_NAME = "channel name for run task"
        const val COMMAND_ID = "command for run task"

        const val COMMAND_START = 1
        const val COMMAND_NEXT = COMMAND_START + 1
        const val COMMAND_CANCEL = COMMAND_START + 2

        @Volatile
        private var instance: ManagerActiveTask? = null

        /**
         * Singleton
         */
        fun getInstance(context: Context): ManagerActiveTask = instance ?: synchronized(this) {
            val res = instance ?: ManagerActiveTask(context)
            Log.d("RunTaskViewFragment", "getInstance $res")
            instance = res
            res
        }
    }
}
