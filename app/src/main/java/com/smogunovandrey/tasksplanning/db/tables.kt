package com.smogunovandrey.tasksplanning.db

import android.content.Context
import android.util.Log
import androidx.room.*
import java.util.*

/**
 * Type trigger
 */
enum class TriggerType{
    HAND, GPS_IN, GPS_OUT, NFC, VOICE, BTN_HEADPHONE
}

/**
 * Trigger for event
 */
//@Entity
//data class Trigger(
//    @PrimaryKey(autoGenerate = true) val id: Long,
//    val type: TriggerType
//)

/**
 * Point for event
 */
@Entity
data class Point(
    @PrimaryKey(autoGenerate = true) val idPoint: Long,
    val name: String,
    val triggerType: TriggerType
)

/**
 * Additional params for points with GPS trigger
 */
@Entity
data class PointGps(
    @PrimaryKey val idPoint: Long,
    val latitude: Float,
    val longitude: Float
)

/**
 * Task item for persist database
 */

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val idTask: Long,
    val name: String
)

@Entity(primaryKeys = ["idTask", "idPoint"])
data class TaskPointCrossRef(
    val idTask: Long,
    val idPoint: Long
)

data class TaskWithPoint(
    @Embedded val task: Task,

    @Relation(parentColumn = "idTask", entityColumn = "idPoint", associateBy = Junction(TaskPointCrossRef::class))
    val listTaskPoint: List<Point>
)

@Entity(primaryKeys = ["idTask", "idPoint"])
data class RunTaskCrossRef(
    val idTask: Long,
    val idPoint: Long,
    val num: Long,
    val dateBegin: Date = Date(),
    val dateEnd: Date = Date()
)

data class RunTaskWithPoint(
    @Embedded val task: Task,

    @Relation(parentColumn = "idTask", entityColumn = "idPoint", associateBy = Junction(RunTaskCrossRef::class))
    val listPoints: List<Point>
)

@Dao
interface MainDao{
    //Task
    @Insert
    suspend fun addTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    //Point
    @Insert
    suspend fun insertPoint(point: Point)

    @Delete
    suspend fun deletePoint(point: Point)

    @Update
    suspend fun updatePoint(point: Point)

    //TaskPoint
    @Insert
    suspend fun insertTaskPoint(taskPoint: TaskPointCrossRef)

    @Delete
    suspend fun deleteTaskPoint(taskPoint: TaskPointCrossRef)

    @Query("select * from Task where idTask = :takId")
    suspend fun pointsByTask(takId: Long): TaskWithPoint?

    @Query("select * FROM Task")
    suspend fun  allTaskWithPoint(): List<TaskWithPoint>
}

@Database(entities = [/*Trigger::class*,*/ Point::class, Task::class, TaskPointCrossRef::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun mainDao(): MainDao

    companion object{
        private val DB_NAME = "task.db"

        @Volatile
        private var instance: AppDatabase? = null

        /**
         * Singleton  with double-check locking. Thread safe and lazy.
         */
        fun getInstance(context: Context): AppDatabase = instance ?: synchronized(this){
            val res =  instance ?: Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()
            instance = res
            res
        }
    }
}

class Converters{
    @TypeConverter
    fun fromTimestamp(value: Long?): Date?{
        return value?.let{
            Date(it)
        }
    }

    @TypeConverter
    fun dateToTimestamp(value: Date?): Long?{
        return value?.time
    }
}

