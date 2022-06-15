package com.smogunovandrey.tasksplanning

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.smogunovandrey.tasksplanning.db.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "tasks.db").build()
        val dao = db.mainDao()

        scope.launch {
            //Insert Test
//            for (i in 1..2) {
//                val task = Task(0, "task $i")
//                val idTask = dao.insertTask(task)
//                dao.insertRunTask(RunTask(0, idTask))
//                for (triggerType in TriggerType.values()) {
//                    val point: Point = Point(0, idTask, "name ${triggerType.name}", triggerType.ordinal.toLong() + 1L, triggerType)
//                    val idPoint = dao.insertPoint(point)
//                    val runPoint = RunPoint(0, idTask, idPoint, dateEnd = Date())
//                    dao.insertRunPoint(runPoint)
//                    delay(1000)
//                    Log.d("MainActivity", "create $runPoint")
//                }
//            }

            dao.runTaskById(1L)?.let {
                for(item in it.listRunPoint)
                    Log.d("MainActivity", item.toString())
            }

              //Test query
//            for(item in dao.allTaskWithPoint()){
//                Log.d("MainActivity", item.toString())
//            }


        }

    }
}