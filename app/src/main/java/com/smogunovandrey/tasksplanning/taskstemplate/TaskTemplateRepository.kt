package com.smogunovandrey.tasksplanning.taskstemplate

import com.smogunovandrey.tasksplanning.db.MainDao
import com.smogunovandrey.tasksplanning.result.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TaskTemplateRepository(private val mainDao: MainDao) {
    val tasksTemplate = mainDao.allTasksFlow().map {
        it.map{taskDB ->
            Task(taskDB.id, taskDB.name)
        }
    }
}