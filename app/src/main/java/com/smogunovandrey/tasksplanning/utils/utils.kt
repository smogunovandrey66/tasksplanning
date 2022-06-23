package com.smogunovandrey.tasksplanning.utils

import com.google.gson.Gson
import com.smogunovandrey.tasksplanning.db.PointDB
import com.smogunovandrey.tasksplanning.db.TaskDB
import com.smogunovandrey.tasksplanning.taskstemplate.Point
import com.smogunovandrey.tasksplanning.taskstemplate.Task

fun <T> MutableList<T>.swap(index1: Int, index2: Int){
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

fun Task.toDB() = TaskDB(id, name)

fun TaskDB.toTask() = Task(id, name)

fun Point.toDB(idTask: Long) = PointDB(id, idTask, name, num, triggerType)

fun PointDB.toPoint() = Point(id, name, num, triggerType)

//fun Any.deepCopy():Any {
//    val JSON = Gson().toJson(this)
//    return Gson().fromJson(JSON, Any::class.java)
//}