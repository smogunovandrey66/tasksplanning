package com.smogunovandrey.tasksplanning.runtask

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.RunTaskWithPointDB
import com.smogunovandrey.tasksplanning.taskstemplate.RunPoint
import com.smogunovandrey.tasksplanning.taskstemplate.RunTask
import com.smogunovandrey.tasksplanning.taskstemplate.RunTaskWithPoints
import java.util.Date

class ManagerActiveTask private constructor(val context: Context) {
    private val dao by lazy {
        AppDatabase.getInstance(context).mainDao()
    }

    suspend fun activiTask(): RunTaskWithPoints?{
        val runTaskWithPointsDBFlow = dao.activeRunTaskWithPointsLiveData()
        val runTaskWithPointsDB = runTaskWithPointsDBFlow.value

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