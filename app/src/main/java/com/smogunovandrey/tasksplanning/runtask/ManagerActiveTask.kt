package com.smogunovandrey.tasksplanning.runtask

import android.content.Context
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.RunPointDB
import com.smogunovandrey.tasksplanning.db.RunTaskDB
import com.smogunovandrey.tasksplanning.db.TaskWithPointDB
import com.smogunovandrey.tasksplanning.taskstemplate.RunPoint
import com.smogunovandrey.tasksplanning.taskstemplate.RunTask
import com.smogunovandrey.tasksplanning.taskstemplate.RunTaskWithPoints
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class ManagerActiveTask private constructor(val context: Context) {
    private val dao by lazy {
        AppDatabase.getInstance(context).mainDao()
    }

    private val _curRunTaskWithPointsFlow = MutableStateFlow<RunTaskWithPoints?>(null)
    val curRunTaskWithPointsFlow: StateFlow<RunTaskWithPoints?> = _curRunTaskWithPointsFlow

    suspend fun reloadActiviTask(): RunTaskWithPoints?{
        val runTaskWithPointsDB = dao.activeRunTaskWithPointsSuspend()


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
        _curRunTaskWithPointsFlow.emit(runTaskWithPoints)
        return runTaskWithPoints
    }

    suspend fun startTask(idTask: Long){
        val taskWithPointsDB: TaskWithPointDB = dao.taskWithPointsSuspend(idTask)
        val taskRunDB = RunTaskDB(0, idTask)
        val idRunTask = dao.insertRunTask(taskRunDB)
        val points = taskWithPointsDB.points.sortedWith{p1, p2 ->
            p1.num - p2.num
        }
        for(pointDB in taskWithPointsDB.points){
            dao.insertRunPoint(RunPointDB(
                0,
                idRunTask,
                pointDB.idPoint,
                pointDB.num,

            ))
        }
    }

    fun markPoint(){ //or name as 'nextPoint'

    }

    fun cancelTask(){

    }

    @Volatile
    private var instance: ManagerActiveTask? = null

    /**
     * Singleton
     */
    fun getInstance(context: Context): ManagerActiveTask = instance ?: synchronized(this){
        val res = instance ?: ManagerActiveTask(context)
        res
    }
}
