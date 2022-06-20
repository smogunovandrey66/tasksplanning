package com.smogunovandrey.tasksplanning.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateRepository

class TaskTemplateViewModel(application: Application) : AndroidViewModel(application) {
    private val taskTemplateRepository = TaskTemplateRepository(AppDatabase.getInstance(application.applicationContext).mainDao())

    val tasksTemplate = taskTemplateRepository.tasksTemplate
}