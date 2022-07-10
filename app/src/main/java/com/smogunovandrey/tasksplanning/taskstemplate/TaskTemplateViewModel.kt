package com.smogunovandrey.tasksplanning.taskstemplate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.smogunovandrey.tasksplanning.db.AppDatabase

class TaskTemplateViewModel(application: Application) : AndroidViewModel(application) {
    private val taskTemplateRepository = TaskTemplateRepository(AppDatabase.getInstance(application.applicationContext).mainDao())

    val tasksTemplate = taskTemplateRepository.tasksTemplate

    fun taskById(idTask: Long)= taskTemplateRepository.taskById(idTask)

    fun pointsTemplate(idTask: Long) = taskTemplateRepository.pointsByTaskId(idTask)

    fun taskWithPoints(idTask: Long) = taskTemplateRepository. taskWithPoints(idTask)

    //Edited TaskWithPoints
    var editedTaskWithPoints = TaskWithPoints()

    //Save TaskWithPoints
    suspend fun updateTaskWithPoints(taskWithPoints: TaskWithPoints) {
        taskTemplateRepository.updateTaskWithPoints(taskWithPoints)
    }

    //Edited point
    var editedPoint = Point()

    /**
     * true - editing on click
     * false - can't editing on click, can delete point by swap left and right,
     *         can move swap up and down
     */
    var editClickPoint = false

    /**
     * false - user click "ADD Point"(create new point)
     * true - user click item point in list points(edit point)
     */
    var flagEditPoint = false

    suspend fun updatePoint(point: Point) = taskTemplateRepository.updatePoint(point)
    suspend fun addPoint(point: Point) = taskTemplateRepository.addPoint(point)

    var selectedPoint: Point = Point()

    /**
     * Position for notify inserted in AdapterEditPoints.
     * -1 not need notify
     */
    var posPointInserted: Int? = null

    /**
     * Position for notify update in AdapterEditPoints.
     * -1 not need notify
     */
    var posPointUpdate: Int? = null

    suspend fun deleteTask(idTask: Long) = taskTemplateRepository.deleteTask(idTask)

    val activeTask = taskTemplateRepository.activeTask()
}