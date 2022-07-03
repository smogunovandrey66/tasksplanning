package com.smogunovandrey.tasksplanning.db

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*


/**
 * Task item for persist database
 */

@Entity(tableName = "tasks")
data class TaskDB(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String
)

/**
 * Type trigger
 */
enum class TriggerType{
    HAND, GPS_IN, GPS_OUT, NFC, VOICE, BTN_HEADPHONE, TIMER
}

/**
 * Point for event
 */
@Entity(tableName = "points")
data class PointDB(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "id_task")
    val idTask: Long,
    val name: String,
    val num: Long,
    val triggerType: TriggerType
)

/**
 * Additional params for points with GPS trigger
 */
@Entity(tableName = "points_gps")
data class PointGpsDB(
    @PrimaryKey
    @ColumnInfo(name = "id_point")
    val idPoint: Long,
    val latitude: Float,
    val longitude: Float
)

data class TaskWithPointDB(
    @Embedded val task: TaskDB,

    @Relation(parentColumn = "id", entityColumn = "id_task")
    val listTaskPoint: List<PointDB>
)

@Entity(tableName = "run_tasks")
data class RunTaskDB(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "id_task")
    val idTask: Long,
    val dateCreate: Date = Date(),
    val active: Boolean = true
)

@Entity(tableName = "run_points")
data class RunPointDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val idRunPoint: Long,
    @ColumnInfo(name = "id_run_task")
    val idRunTask: Long,
    @ColumnInfo(name = "id_point")
    val idPoint: Long,
    val num: Long,
    @ColumnInfo(name = "date_mark")
    var dateMark: Date? = null
)

data class RunTaskWithPointDB(
    @Embedded val runTask: RunTaskDB,

    @Relation(parentColumn = "id", entityColumn = "id_run_task")
    val listRunPoint: MutableList<RunPointDB>
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
    suspend fun insertTask(task: TaskDB): Long

    @Delete
    suspend fun deleteTask(task: TaskDB)

    @Update
    suspend fun updateTask(task: TaskDB)

    @Query("select * from tasks")
    fun allTasksFlow(): Flow<List<TaskDB>>

    @Query("select * from tasks")
    suspend fun allTasksSuspend(): List<TaskDB>

    @Query("select * from tasks where id = :idTask")
    fun taskById(idTask: Long): Flow<TaskDB?>

    @Query("select * from tasks where id = :idTask")
    suspend fun taskByIdSuspend(idTask: Long): TaskDB

    //Point
    @Insert
    suspend fun insertPoint(point: PointDB): Long

    @Delete
    suspend fun deletePoint(point: PointDB)

    @Update
    suspend fun updatePoint(point: PointDB)

    @Query("select * from points where id_task = :idTask")
    fun pointsByTaskId(idTask: Long): Flow<List<PointDB>>


    //GPS point
    @Query("select * from points_gps where id_point = :idPoint")
    suspend fun gpsPoint(idPoint: Long): PointGpsDB?

    //TaskWithPointDB
    @Query("select * from tasks where id = :takId")
    fun taskWithPoints(takId: Long): Flow<TaskWithPointDB>

    @Query("select * from tasks where id = :idTask")
    suspend fun taskWithPointsSuspend(idTask: Long): TaskWithPointDB

    @Query("select * FROM tasks")
    fun  allTaskWithPoint(): Flow<List<TaskWithPointDB>>

    //RunTask
    @Insert
    suspend fun insertRunTask(runTask: RunTaskDB): Long

    @Delete
    suspend fun deleteRunTask(runTask: RunTaskDB)

    //RunPoint
    @Insert
    suspend fun insertRunPoint(runPoint: RunPointDB)

    @Delete
    suspend fun deleteRunPoint(runPoint: RunPointDB)


    //RunTaskWithPoint
    @Query("select * from run_tasks")
    suspend fun allRunTask(): List<RunTaskWithPointDB>

    @Query("select * from run_tasks where id = :idRunTask")
    suspend fun runTaskWithPointsByIdSuspend(idRunTask: Long): RunTaskWithPointDB
}

@Database(entities = [PointDB::class, PointGpsDB::class, TaskDB::class, RunPointDB::class, RunTaskDB::class], version = 1)
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

