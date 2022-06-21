package com.smogunovandrey.tasksplanning.taskstemplate

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateRepository
import kotlinx.coroutines.flow.Flow

class TaskTemplateViewModel(application: Application) : AndroidViewModel(application) {
    private val taskTemplateRepository = TaskTemplateRepository(AppDatabase.getInstance(application.applicationContext).mainDao())

    val tasksTemplate = taskTemplateRepository.tasksTemplate

    fun taskById(idTask: Long)= taskTemplateRepository.taskById(idTask)

    fun pointsTemplate(idTask: Long) = taskTemplateRepository.pointsByTaskId(idTask)

}