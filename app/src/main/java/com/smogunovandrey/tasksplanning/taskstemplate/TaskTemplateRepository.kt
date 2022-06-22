package com.smogunovandrey.tasksplanning.taskstemplate

import com.smogunovandrey.tasksplanning.db.MainDao
import com.smogunovandrey.tasksplanning.db.TaskDB
import com.smogunovandrey.tasksplanning.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class TaskTemplateRepository(private val mainDao: MainDao) {
    val tasksTemplate = mainDao.allTasksFlow().map {
        it.map{taskDB ->
            Task(taskDB.id, taskDB.name)
        }
    }

    fun pointsByTaskId(idTask: Long): Flow<List<Point>> = mainDao.pointsByTaskId(idTask).map { listPointsDB ->
        listPointsDB.map{
            Point(it.id, it.name, it.num, it.triggerType)
        }
    }

    fun taskById(idTask: Long): Flow<Task?> = mainDao.taskById(idTask).map { taskDB ->
        taskDB?.let {
            Task(taskDB.id, taskDB.name)
        }
    }

    fun taskWithPoints(idTask: Long): Flow<TaskWithPoints> = mainDao.taskWithPoints(idTask).map { it ->
        TaskWithPoints(Task(it.task.id, it.task.name), it.listTaskPoint.map { pointDB ->
            Point(pointDB.id, pointDB.name, pointDB.num, pointDB.triggerType)
        }.toMutableList())
    }
}