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
            Task(taskDB.idTask, taskDB.name)
        }
    }

    fun pointsByTaskId(idTask: Long): Flow<List<Point>> = mainDao.pointsByTaskId(idTask).map { listPointsDB ->
        listPointsDB.map{
            Point(it.idPoint, idTask, it.name, it.num, it.triggerType)
        }
    }

    fun taskById(idTask: Long): Flow<Task?> = mainDao.taskById(idTask).map { taskDB ->
        taskDB?.let {
            Task(taskDB.idTask, taskDB.name)
        }
    }

    /**
     * Task with Points sorted by num
     */
    fun taskWithPoints(idTask: Long): Flow<TaskWithPoints> = mainDao.taskWithPoints(idTask).map {
        TaskWithPoints(Task(it.task.idTask, it.task.name), it.points.map { pointDB ->
            Point(pointDB.idPoint, idTask, pointDB.name, pointDB.num, pointDB.triggerType)
        }.sortedWith { point1, point2 -> (point1.num - point2.num).toInt() }.toMutableList())
    }

    suspend fun updateTaskWithPoints(taskWithPoints: TaskWithPoints){
        val task = taskWithPoints.task
        val points: MutableList<Point> = taskWithPoints.points

        //1. Update Task
        if(task.id == 0L){
            val idTask = mainDao.insertTask(task.toTaskDB())
            task.id = idTask
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
                point.id = mainDao.insertPoint(point.toPointDB())
            } else{
                mainDao.updatePoint(point.toPointDB())
            }
        }

        val iterator = pointsInDB.iterator()
        while (iterator.hasNext()){
            val pointDB = iterator.next()

            var find = false
            for(point in points){
                if(pointDB.id == point.id){
                    find = true
                    break
                }
            }

            if(!find){
                mainDao.deletePoint(pointDB.toPointDB())
            }
        }
    }

    suspend fun updatePoint(point: Point) = mainDao.updatePoint(point.toPointDB())

    suspend fun addPoint(point: Point) = mainDao.insertPoint(point.toPointDB())

    suspend fun deleteTask(idTask: Long){
        val taskWithPointDB = mainDao.taskWithPointsSuspend(idTask)
        for(point in taskWithPointDB.points)
            mainDao.deletePoint(point)
        mainDao.deleteTask(taskWithPointDB.task)
    }

    fun activeTask() = mainDao.activeTask()
}