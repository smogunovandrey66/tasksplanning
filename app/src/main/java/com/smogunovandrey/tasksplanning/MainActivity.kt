package com.smogunovandrey.tasksplanning

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.smogunovandrey.tasksplanning.db.*
import com.smogunovandrey.tasksplanning.viewmodel.TaskTemplateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskTemplateViewModel: TaskTemplateViewModel by viewModels()

        setContentView(R.layout.activity_main)

//        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "tasks.db").build()
//        val dao = db.mainDao()
        val t = taskTemplateViewModel
        lifecycleScope.launch {
            Log.d("MainActivity", "MainActivity")
            taskTemplateViewModel.tasksTemplate.collect{
                for(item in it)
                    Log.d("MainActivity", item.toString())
            }
            Log.d("MainActivity", "in MainActivity")
            //Insert Test
            for (i in 1..2) {
                delay(3000)
                val task = Task(0, "task $i")
                val dao = AppDatabase.getInstance(applicationContext).mainDao()
                val idTask = dao.insertTask(task)
                dao.insertRunTask(RunTask(0, idTask))
                for (triggerType in TriggerType.values()) {
                    val point: Point = Point(0, idTask, "name ${triggerType.name}", triggerType.ordinal.toLong() + 1L, triggerType)
                    val idPoint = dao.insertPoint(point)
                    val runPoint = RunPoint(0, idTask, idPoint, dateEnd = Date())
                    dao.insertRunPoint(runPoint)
                    delay(1000)
                    Log.d("MainActivity", "create $runPoint")
                }
            }

            //Test get
//            dao.runTaskById(1L)?.let {
//                for(item in it.listRunPoint)
//                    Log.d("MainActivity", item.toString())
//            }

        }

    }
}