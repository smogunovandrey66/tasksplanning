package com.smogunovandrey.tasksplanning.utils

import android.os.Bundle
import com.smogunovandrey.tasksplanning.db.*
import com.smogunovandrey.tasksplanning.taskstemplate.*
import java.util.*

/**
 * Swap for MutableList
 */
fun <T> MutableList<T>.swap(index1: Int, index2: Int){
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}
//***********Convert function DB<->not DB************************

fun Task.toTaskDB() = TaskDB(id, name)

fun TaskDB.toTask() = Task(idTask, name)

fun Point.toPointDB() = PointDB(id, idTask, name, num, triggerType)

fun PointDB.toPoint() = Point(idPoint, idTask, name, num, triggerType)

fun PointDB.toRunPoint(idRunPoint: Long = 0, duration: Long = 0, dateMark: Date? = null) =
    RunPoint(idRunPoint, idTask, idPoint, num, name, triggerType, duration, dateMark)

fun Point.toRunPoint(idRunPoint: Long = 0, duration: Long = 0, dateMark: Date? = null) =
    RunPoint(idRunPoint, idTask, id, num, name, triggerType, duration, dateMark)

fun List<PointDB>.toListPoint() = map{
    it.toPoint()
}
fun RunPointDB.toRunPoint(idTask: Long = 0, num: Int = 0, name: String = "") =
    RunPoint(idRunPoint, idTask, idPoint, num, name)

fun RunTask.toRunTaskDB() =
    RunTaskDB(idRunTask, idTask, dateCreate ?: Date(), active)

fun List<Point>.toListPointDB() = map{
    it.toPointDB()
}
fun RunPoint.toRunPointDB(idRunTask: Long) =
    RunPointDB(idRunPoint, idRunTask, idPoint, num, dateMark)

fun TaskWithPointDB.toTaskWithPoint() = TaskWithPoints(this.task.toTask(), this.points.toListPoint().toMutableList())

fun TaskWithPoints.toTaskWithPointDB() = TaskWithPointDB(this.task.toTaskDB(), this.points.toListPointDB())

//***********Convert function DB<->not DB************************

/**
 * List-view presentation bundle
 */
fun Bundle?.toList(): kotlin.collections.List<String>? =
    if(this != null){
        keySet().map { key ->
            "$key=${get(key)}"
        }
    } else {
        null
    }

//fun Any.deepCopy():Any {
//    val JSON = Gson().toJson(this)
//    return Gson().fromJson(JSON, Any::class.java)
//}