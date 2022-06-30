package com.smogunovandrey.tasksplanning.runtask

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.TaskWithPointDB
import com.smogunovandrey.tasksplanning.taskstemplate.RunTask
import com.smogunovandrey.tasksplanning.taskstemplate.RunTaskWithPoints
import com.smogunovandrey.tasksplanning.taskstemplate.Task
import com.smogunovandrey.tasksplanning.utils.toTask
import kotlinx.coroutines.flow.MutableStateFlow

//Need whether repository???

sealed class Result{
    class Success<T>(val data: T): Result() {

    }
}

class RunTaskViewModel(application: Application) : AndroidViewModel(application) {
    private val _curRunTaskWithPoints: MutableStateFlow<Result> = MutableStateFlow(
        Result.Success(RunTaskWithPoints())
    )
//    val curRunTaskWithPoints: Flow<>

    private val dao by lazy {
        AppDatabase.getInstance(application.applicationContext).mainDao()
    }

    suspend fun prepareTask(idTask: Long){
        val data = RunTaskWithPoints(RunTask(idTask = idTask))

        val taskWithPoints = dao.taskWithPointsSuspend(idTask)
        data.runTask = RunTask(0, idTask, taskWithPoints.task.name)


        _curRunTaskWithPoints.emit(Result.Success(data))
    }
}