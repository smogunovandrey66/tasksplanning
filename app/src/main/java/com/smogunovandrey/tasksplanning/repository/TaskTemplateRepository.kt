package com.smogunovandrey.tasksplanning.repository

import com.smogunovandrey.tasksplanning.db.MainDao
import com.smogunovandrey.tasksplanning.result.Result
import kotlinx.coroutines.flow.MutableSharedFlow

class TaskTemplateRepository(private val mainDao: MainDao) {
    val tasksTemplate = mainDao.allTaskWithPoint()
}