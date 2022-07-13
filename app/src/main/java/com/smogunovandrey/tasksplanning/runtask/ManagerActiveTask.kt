package com.smogunovandrey.tasksplanning.runtask

import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.RunPointDB
import com.smogunovandrey.tasksplanning.db.RunTaskDB
import com.smogunovandrey.tasksplanning.db.TaskWithPointDB
import com.smogunovandrey.tasksplanning.taskstemplate.RunPoint
import com.smogunovandrey.tasksplanning.taskstemplate.RunTask
import com.smogunovandrey.tasksplanning.taskstemplate.RunTaskWithPoints
import com.smogunovandrey.tasksplanning.utils.toRunPointDB
import com.smogunovandrey.tasksplanning.utils.toRunTaskDB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class ManagerActiveTask private constructor(val context: Context) {
    private val dao by lazy {
        AppDatabase.getInstance(context).mainDao()
    }

    private val _activeRunTaskWithPointsFlow = MutableStateFlow<RunTaskWithPoints?>(null)
    val activeRunTaskWithPointsFlow: StateFlow<RunTaskWithPoints?> = _activeRunTaskWithPointsFlow


    suspend fun reloadActiviTask(idRunTask: Long){

    }
    suspend fun getRunTask(idRunTask: Long): RunTaskWithPoints?{
        val runTaskWithPointsDB = dao.runTaskWithPointsByIdSuspend(idRunTask)

        var runTaskWithPoints: RunTaskWithPoints? = null

        if(runTaskWithPointsDB != null){
            val runTaskDB = runTaskWithPointsDB.runTask
            val taskDB = dao.taskByIdSuspend(runTaskDB.idTask)
            runTaskWithPoints = RunTaskWithPoints(
                RunTask(
                    runTaskDB.idRunTask,
                    runTaskDB.idTask,
                    taskDB.name,
                    runTaskDB.dateCreate,
                    runTaskDB.active
                ),
                runTaskWithPointsDB.runPoints.map {
                    val pointDB = dao.pointByIdSuspend(it.idPoint)
                    var dateMark = 0L
                    it.dateMark?.let {
                        dateMark = it.time - Date().time
                    }
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
        }

        return runTaskWithPoints
    }

    suspend fun startTask(idTask: Long){
        val taskWithPointsDB: TaskWithPointDB = dao.taskWithPointsSuspend(idTask)
        val idRunTask = dao.insertRunTask(RunTaskDB(0, idTask))
        val points = taskWithPointsDB.points.sortedWith{p1, p2 ->
            p1.num - p2.num
        }
        for(pointDB in points){
            dao.insertRunPoint(RunPointDB(
                0,
                idRunTask,
                pointDB.idPoint,
                pointDB.num
            ))
            context.send
        }
        _activeRunTaskWithPointsFlow.emit(getRunTask(idRunTask))
    }

    suspend fun markPoint(){ //or name as 'nextPoint'
        val runTaskWithPoints = _activeRunTaskWithPointsFlow.value
        runTaskWithPoints?.let {
            var curIdx = 0
            var lastIdx = it.points.size - 1
            for(point in it.points){
                if(point.dateMark == null){
                    point.dateMark = Date()
                    val idRunTask = runTaskWithPoints.runTask.idRunTask
                    dao.updateRunPoint(point.toRunPointDB(idRunTask))

                    //Set active to false for RunTask if last point
                    if(curIdx == lastIdx){
                        dao.updateRunTask(runTaskWithPoints.runTask.toRunTaskDB())
                    }

                    _activeRunTaskWithPointsFlow.emit(getRunTask(idRunTask))
                    break
                }
                curIdx++
            }
        }
    }

    suspend fun cancelTask(){
        val runTaskWithPoints = _activeRunTaskWithPointsFlow.value
        runTaskWithPoints?.let {
            for(point in it.points){
                val idRunTask = runTaskWithPoints.runTask.idRunTask
                dao.deleteRunPoint(point.toRunPointDB(idRunTask))
            }

            dao.deleteRunTask(runTaskWithPoints.runTask.toRunTaskDB())
        }
    }

    companion object {
        @Volatile
        private var instance: ManagerActiveTask? = null

        /**
         * Singleton
         */
        fun getInstance(context: Context): ManagerActiveTask = instance ?: synchronized(this) {
            val res = instance ?: ManagerActiveTask(context)
            res
        }
    }
}
