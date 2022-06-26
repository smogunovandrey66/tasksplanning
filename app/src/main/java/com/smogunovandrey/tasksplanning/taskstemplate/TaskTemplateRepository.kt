package com.smogunovandrey.tasksplanning.taskstemplate

import com.smogunovandrey.tasksplanning.db.MainDao
import com.smogunovandrey.tasksplanning.utils.toPointDB
import com.smogunovandrey.tasksplanning.utils.toTaskDB
import com.smogunovandrey.tasksplanning.utils.toTaskWithPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskTemplateRepository(private val mainDao: MainDao) {
    val tasksTemplate = mainDao.allTasksFlow().map {
        it.map{taskDB ->
            Task(taskDB.id, taskDB.name)
        }
    }

    fun pointsByTaskId(idTask: Long): Flow<List<Point>> = mainDao.pointsByTaskId(idTask).map { listPointsDB ->
        listPointsDB.map{
            Point(it.id, idTask, it.name, it.num, it.triggerType)
        }
    }

    fun taskById(idTask: Long): Flow<Task?> = mainDao.taskById(idTask).map { taskDB ->
        taskDB?.let {
            Task(taskDB.id, taskDB.name)
        }
    }

    /**
     * Task with Points sorted by num
     */
    fun taskWithPoints(idTask: Long): Flow<TaskWithPoints> = mainDao.taskWithPoints(idTask).map {
        TaskWithPoints(Task(it.task.id, it.task.name), it.listTaskPoint.map { pointDB ->
            Point(pointDB.id, idTask, pointDB.name, pointDB.num, pointDB.triggerType)
        }.sortedWith { point1, point2 -> (point1.num - point2.num).toInt() }.toMutableList())
    }

    suspend fun updateTaskWithPoints(taskWithPoints: TaskWithPoints){
        val task = taskWithPoints.task
        val points: List<Point> = taskWithPoints.points

        //1. Update Task
        if(task.id == 0L){
            val idTask = mainDao.insertTask(task.toTaskDB())
            for(point in points)
                point.idTask = idTask
        } else {
            mainDao.updateTask(task.toTaskDB())
        }

        val taskWithPointsInDB = mainDao.taskWithPointsSuspend(task.id).toTaskWithPoint()
        val pointsInDB = taskWithPointsInDB.points.toList()

        //2. Add, update, remove points
        for(point in points){
            //Insert
            if(point.id == 0L){
                mainDao.insertPoint(point.toPointDB())
            } else{
                var find = false
                for(pointInDB in pointsInDB){
                    if(pointInDB.id == point.id){
                        find = true
                        break
                    }
                }

                if(find)
                    //Update
                    mainDao.updatePoint(point.toPointDB())
                else
                    //Delete
                    mainDao.deletePoint(point.toPointDB())
            }
        }
    }

    suspend fun updatePoint(point: Point) = mainDao.updatePoint(point.toPointDB())

    suspend fun addPoint(point: Point) = mainDao.insertPoint(point.toPointDB())

}