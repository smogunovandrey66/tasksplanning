package com.smogunovandrey.tasksplanning.taskstemplate

import com.google.gson.Gson
import com.smogunovandrey.tasksplanning.db.TriggerType

data class Point(
    var id: Long,
    var name: String,
    var num: Long,
    var triggerType: TriggerType
)


data class Task(
    var id: Long = 0,
    var name: String = ""
)

data class TaskWithPoints(
    var task: Task = Task(),
    var points: MutableList<Point> = mutableListOf()
){
    //TODO try make generics or Any
    fun deepCopy():TaskWithPoints {
        val JSON = Gson().toJson(this)
        return Gson().fromJson(JSON, TaskWithPoints::class.java)
    }
}

