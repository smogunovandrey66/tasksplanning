package com.smogunovandrey.tasksplanning.utils

import com.smogunovandrey.tasksplanning.db.PointDB
import com.smogunovandrey.tasksplanning.db.TaskDB
import com.smogunovandrey.tasksplanning.db.TaskWithPointDB
import com.smogunovandrey.tasksplanning.taskstemplate.Point
import com.smogunovandrey.tasksplanning.taskstemplate.Task
import com.smogunovandrey.tasksplanning.taskstemplate.TaskWithPoints

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

fun TaskDB.toTask() = Task(id, name)

fun Point.toPointDB() = PointDB(id, idTask, name, num, triggerType)

fun PointDB.toPoint() = Point(id, idTask, name, num, triggerType)


fun List<PointDB>.toListPoint() = map{
    it.toPoint()
}

fun List<Point>.toListPointDB() = map{
    it.toPointDB()
}

fun TaskWithPointDB.toTaskWithPoint() = TaskWithPoints(this.task.toTask(), this.listTaskPoint.toListPoint().toMutableList())

fun TaskWithPoints.toTaskWithPointDB() = TaskWithPointDB(this.task.toTaskDB(), this.points.toListPointDB())

//***********Convert function DB<->not DB************************

//fun Any.deepCopy():Any {
//    val JSON = Gson().toJson(this)
//    return Gson().fromJson(JSON, Any::class.java)
//}