package com.smogunovandrey.tasksplanning.runtask

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.RunPointDB
import com.smogunovandrey.tasksplanning.taskstemplate.RunPoint
import com.smogunovandrey.tasksplanning.taskstemplate.RunTask
import com.smogunovandrey.tasksplanning.taskstemplate.RunTaskWithPoints
import com.smogunovandrey.tasksplanning.utils.toRunPoint
import com.smogunovandrey.tasksplanning.utils.toRunPointDB
import com.smogunovandrey.tasksplanning.utils.toRunTaskDB
import com.smogunovandrey.tasksplanning.utils.toTaskWithPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

//Need whether repository???

sealed class Result{
    class Success<T>(val data: T): Result() {

    }
}

class RunTaskViewModel(application: Application) : AndroidViewModel(application) {
    private val _curRunTaskWithPoints: MutableStateFlow<RunTaskWithPoints> = MutableStateFlow(
        RunTaskWithPoints()
    )
    val curRunTaskWithPoints: Flow<RunTaskWithPoints> = _curRunTaskWithPoints
    //Working data
    val workingRunTaskWithPoints: RunTaskWithPoints = RunTaskWithPoints()

    private val dao by lazy {
        AppDatabase.getInstance(application.applicationContext).mainDao()
    }

    var curIdRunTask = 0L
    var curIdTask = 0L

    suspend fun loadRunTask(){
        val idTask: Long = curIdTask
        val idRunTask: Long = curIdRunTask
        val cashData = _curRunTaskWithPoints.value.runTask
        if((cashData.id == idRunTask) && (cashData.idTask == idTask))
            return

        //Template
        val taskWithPoints = dao.taskWithPointsSuspend(idTask).toTaskWithPoint()
        val task = taskWithPoints.task
        val points = taskWithPoints.points
        //Sort by number
        points.sortWith { p1, p2 -> (p1.num - p2.num).toInt() }

        //Fill template
        val data = RunTaskWithPoints(RunTask(idRunTask, task.id, task.name))
        for(point in points){
            data.points.add(point.toRunPoint())
        }
        //Sort by number
        data.points.sortWith(Comparator{p1, p2 -> (p1.num - p2.num).toInt()})

        if(idRunTask > 0){
            var runTaskDB = dao.runTaskWithPointsByIdSuspend(idRunTask)
            data.runTask.active = runTaskDB.runTask.active
            data.runTask.dateCreate = runTaskDB.runTask.dateCreate

            runTaskDB.listRunPoint.sortWith{p1, p2 -> (p1.num - p2.num).toInt()}

            for(i in 0 until data.points.size){
                val pointDB: RunPointDB = runTaskDB.listRunPoint[i]
                val point:  RunPoint = data.points[i]

                //Need equals
                if(point.num != pointDB.num)
                    continue

                point.dateMark = pointDB.dateMark
                point.dateMark?.let {
                    point.duration = it.time - runTaskDB.runTask.dateCreate.time
                }
            }
        }

        _curRunTaskWithPoints.emit(data)
    }

    fun runTask(runTaskWithPoints: RunTaskWithPoints){
        viewModelScope.launch {
            val idRunTask = dao.insertRunTask(runTaskWithPoints.runTask.toRunTaskDB())
            for(point in runTaskWithPoints.points){
                dao.insertRunPoint(point.toRunPointDB(idRunTask))
            }
            curIdRunTask = idRunTask
            curIdTask = runTaskWithPoints.runTask.idTask
            loadRunTask()
        }
    }
}