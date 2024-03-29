package com.smogunovandrey.tasksplanning.taskstemplate

import com.google.gson.Gson
import com.smogunovandrey.tasksplanning.db.TriggerType
import java.util.*


data class Point(
    var id: Long = 0L,
    var idTask: Long = 0L,
    var name: String = "",
    var num: Int = 0,
    var triggerType: TriggerType = TriggerType.HAND,
    var gpsPoint: com.yandex.mapkit.geometry.Point? = null
) {
    fun clear() {
        id = 0L
        idTask = 0L
        name = ""
        num = 0
        triggerType = TriggerType.HAND
    }

    override fun equals(other: Any?): Boolean {
        if(other is Point){
            var res = other.id == id &&
                    other.num == num &&
                    other.idTask == idTask &&
                    other.name == name &&
                    other.triggerType == triggerType
            if(res){
                res = if(other.gpsPoint != null && gpsPoint != null){
                    other.gpsPoint!!.latitude == gpsPoint!!.latitude &&
                            other.gpsPoint!!.longitude == gpsPoint!!.longitude
                }  else other.gpsPoint == null && gpsPoint == null
            }
            return res
        }
        return false
    }

    fun copy(other: Point): Point {
        id = other.id
        idTask = other.idTask
        name = other.name
        num = other.num
        triggerType = other.triggerType
        if (other.gpsPoint != null)
            gpsPoint = com.yandex.mapkit.geometry.Point(
                other.gpsPoint!!.latitude,
                other.gpsPoint!!.longitude
            )
        else
            gpsPoint = null
        return this
    }
}


data class Task(
    var id: Long = 0,
    var name: String = "",
    var containsGps: Boolean = false
) {
    fun clear() {
        id = 0
        name = ""
    }
}

data class TaskWithPoints(
    var task: Task = Task(),
    var points: MutableList<Point> = mutableListOf()
) {
    //TODO try make generics or Any
    fun deepCopy(): TaskWithPoints {
        val JSON = Gson().toJson(this)
        return Gson().fromJson(JSON, TaskWithPoints::class.java)
    }

    fun clear() {
        task.clear()
        points.clear()
    }
}

data class RunPoint(
    var idRunPoint: Long = 0,
    var idTask: Long = 0,
    var idPoint: Long = 0,
    var num: Int = 0,
    var name: String = "",
    var triggerType: TriggerType = TriggerType.HAND,
    var duration: Long = 0,
    var dateMark: Date? = null
)

data class RunTask(
    var idRunTask: Long = 0L,
    var idTask: Long = 0,
    var name: String = "",
    var dateCreate: Date? = null,
    var active: Boolean = false
)

data class RunTaskWithPoints(
    var runTask: RunTask = RunTask(),
    val points: MutableList<RunPoint> = mutableListOf()
) {
    fun curPoint(): RunPoint? {
        for (point in points) {
            if (point.dateMark == null)
                return point
        }
        return null
    }
}


