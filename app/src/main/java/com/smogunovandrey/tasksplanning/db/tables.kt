package com.smogunovandrey.tasksplanning.db

import android.content.Context
import android.util.Log
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*


/**
 * Task item for persist database
 */

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val idTask: Long,
    val name: String
)

/**
 * Type trigger
 */
enum class TriggerType{
    HAND, GPS_IN, GPS_OUT, NFC, VOICE, BTN_HEADPHONE
}

/**
 * Point for event
 */
@Entity
data class Point(
    @PrimaryKey(autoGenerate = true) val idPoint: Long,
    val idTask: Long,
    val name: String,
    val num: Long,
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

data class TaskWithPoint(
    @Embedded val task: Task,

    @Relation(parentColumn = "idTask", entityColumn = "idTask")
    val listTaskPoint: List<Point>
)

@Entity
data class RunTask(
    @PrimaryKey(autoGenerate = true) val idRunTask: Long,
    val idTask: Long,
    val active: Boolean = false
)

@Entity
data class RunPoint(
    @PrimaryKey(autoGenerate = true) val idRunPoint: Long,
    val idRunTask: Long,
    val idPoint: Long,
    var dateBegin: Date = Date(),
    var dateEnd: Date? = null
)

data class RunTaskWithPoint(
    @Embedded val runPoint: RunTask,

    @Relation(parentColumn = "idRunTask", entityColumn = "idRunTask")
    val listRunPoint: List<RunPoint>
)

//data class RunPointAndNameAndTrigger(
//    @Embedded val point: RunPointTemplate,
//    val name: String,
//    val triggerType: TriggerType
//)

//data class RunTask(
//    @Embedded val task: Task,
//    @Relation(parentColumn = "idTask", entityColumn = "idTask")
//    val point: RunPointAndNameAndTrigger
//)

@Dao
interface MainDao{
    //Task
    @Insert
    suspend fun insertTask(task: Task): Long

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    //Point
    @Insert
    suspend fun insertPoint(point: Point): Long

    @Delete
    suspend fun deletePoint(point: Point)

    @Update
    suspend fun updatePoint(point: Point)

    //TaskWithPoint
    @Query("select * from Task where idTask = :takId")
    suspend fun taskById(takId: Long): TaskWithPoint?

    @Query("select * FROM Task")
    fun  allTaskWithPoint(): Flow<List<TaskWithPoint>>

    //RunTask
    @Insert
    suspend fun insertRunTask(runTask: RunTask): Long

    @Delete
    suspend fun deleteRunTask(runTask: RunTask)

    //RunPoint
    @Insert
    suspend fun insertRunPoint(runPoint: RunPoint)

    @Delete
    suspend fun deleteRunPoint(runPoint: RunPoint)


    //RunTaskWithPoint
    @Query("select * from RunTask")
    suspend fun allRunTask(): List<RunTaskWithPoint>

    @Query("select * from RunTask where idRunTask = :idRunTask")
    suspend fun runTaskById(idRunTask: Long): RunTaskWithPoint?
}

@Database(entities = [Point::class, Task::class, RunPoint::class, RunTask::class], version = 1)
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

