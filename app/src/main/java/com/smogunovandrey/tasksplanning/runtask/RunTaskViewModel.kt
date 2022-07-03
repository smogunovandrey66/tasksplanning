package com.smogunovandrey.tasksplanning.runtask

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.RunPointDB
import com.smogunovandrey.tasksplanning.taskstemplate.RunPoint
import com.smogunovandrey.tasksplanning.taskstemplate.RunTask
import com.smogunovandrey.tasksplanning.taskstemplate.RunTaskWithPoints
import com.smogunovandrey.tasksplanning.utils.toRunPoint
import com.smogunovandrey.tasksplanning.utils.toTaskWithPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

//Need whether repository???

sealed class Result{
    class Success<T>(val data: T): Result() {

    }
}

class RunTaskViewModel(application: Application) : AndroidViewModel(application) {
    private val _loadedRunTaskWithPoints: MutableStateFlow<RunTaskWithPoints> = MutableStateFlow(
        RunTaskWithPoints()
    )
    val loadedRunTaskWithPoints: Flow<RunTaskWithPoints> = _loadedRunTaskWithPoints
    //Working data
    val workingRunTaskWithPoints: RunTaskWithPoints = RunTaskWithPoints()

    private val dao by lazy {
        AppDatabase.getInstance(application.applicationContext).mainDao()
    }

    suspend fun loadRunTask(idTask: Long, idRunTask: Long = 0L){
        val cashData = _loadedRunTaskWithPoints.value.runTask
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

        _loadedRunTaskWithPoints.emit(data)
    }
}