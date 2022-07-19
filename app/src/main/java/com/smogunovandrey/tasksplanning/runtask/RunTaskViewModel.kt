package com.smogunovandrey.tasksplanning.runtask

import android.app.Application
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

//Need whether repository???

sealed class Result {
    class Success<T>(val data: T) : Result() {

    }
}

class RunTaskViewModel(application: Application) : AndroidViewModel(application) {
    private val _curRunTaskWithPoints: MutableStateFlow<RunTaskWithPoints> =
        MutableStateFlow(
            RunTaskWithPoints()
        )

    val curRunTaskWithPoints: StateFlow<RunTaskWithPoints> = _curRunTaskWithPoints

    private val dao by lazy {
        AppDatabase.getInstance(application.applicationContext).mainDao()
    }

    private val managerActiveTask by lazy {
        ManagerActiveTask.getInstance(application.applicationContext)
    }

    suspend fun loadRunTask(idRunTask: Long) {
        withContext(Dispatchers.Default){
            _curRunTaskWithPoints.value = managerActiveTask.getRunTask(idRunTask)
        }
    }

    fun idRunTask() = curRunTaskWithPoints.value.runTask.idRunTask
}