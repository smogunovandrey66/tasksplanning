package com.smogunovandrey.tasksplanning.db

import androidx.room.*

/**
 * Type trigger
 */
enum class TriggerType{
    HAND, GPS_IN, GPS_OUT, NFC, VOICE, BTN_HEADPHONE
}

/**
 * Trigger for event
 */
@Entity
data class Trigger(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val type: TriggerType
)

/**
 * Point for event
 */
@Entity
data class Point(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val triggerType: TriggerType
)

/**
 * Additional params for points with GPS trigger
 */
@Entity
data class PointGps(
    val idPoint: Long,
    val latitude: Float,
    val longitude: Float
)

/**
 * Task item for persist database
 */

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String
)

@Entity(foreignKeys = [ForeignKey(entity = Task::class, parentColumns = arrayOf("id"),
                                    childColumns = arrayOf("idTask"),
                                    onDelete = ForeignKey.CASCADE),
                      ForeignKey(entity =  Point::class, parentColumns = ["id"],
                                    childColumns = ["idPoint"],
                                    onDelete = ForeignKey.CASCADE)]
)
data class TaskPoint(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val idTask: Long,
    val idPoint: Long
)



@Dao
interface MainDao{
    @Query("select * FROM TaskPoint")
//    fun allTask(): Map<Task, List<Point>>
    fun  allTask(): List<TaskPoint>
}

@Database(entities = [Trigger::class, Point::class, Task::class, TaskPoint::class], version = 1)
abstract class AppDatabase: RoomDatabase(){
    abstract fun mainDao(): MainDao
}

