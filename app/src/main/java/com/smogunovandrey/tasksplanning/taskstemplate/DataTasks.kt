package com.smogunovandrey.tasksplanning.taskstemplate

import com.google.gson.Gson
import com.smogunovandrey.tasksplanning.db.TriggerType
import java.util.*

data class Point(
    var id: Long = 0L,
    var idTask: Long = 0L,
    var name: String = "",
    var num: Long = 0L,
    var triggerType: TriggerType = TriggerType.HAND
){
    fun clear(){
        id = 0L
        idTask = 0L
        name = ""
        num = 0L
        triggerType = TriggerType.HAND
    }
    fun copy(other: Point): Point{
        id = other.id
        idTask = other.idTask
        name = other.name
        num = other.num
        triggerType = other.triggerType
        return this
    }
}


data class Task(
    var id: Long = 0,
    var name: String = ""
){
    fun clear(){
        id = 0
        name = ""
    }
}

data class TaskWithPoints(
    var task: Task = Task(),
    var points: MutableList<Point> = mutableListOf()
){
    //TODO try make generics or Any
    fun deepCopy():TaskWithPoints {
        val JSON = Gson().toJson(this)
        return Gson().fromJson(JSON, TaskWithPoints::class.java)
    }

    fun clear(){
        task.clear()
        points.clear()
    }
}

data class RunPoint(
    var id: Long = 0,
    var idPoint: Long = 0,
    var num: Long = 0,
    var name: String = "",
    var duration: Long = 0,
    var triggerType: TriggerType = TriggerType.HAND,
    var dateMark: Date? = null
)

data class RunTask(
    var id: Long = 0L,
    var idTask: Long = 0,
    var name: String = "",
    var dateCreate: Date = Date(),
    var active: Boolean = false
)

data class RunTaskWithPoints(
    var task: RunTask = RunTask(),
    val points: MutableList<RunPoint> = mutableListOf()
)


