package com.smogunovandrey.tasksplanning

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.smogunovandrey.tasksplanning.databinding.ActivityMainBinding
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.TaskDB
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    val taskTemplateViewModel: TaskTemplateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

//        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "tasks.db").build()
//        val dao = db.mainDao()

        lifecycleScope.launch {
//            return@launch
////            delay(5000)
//            taskTemplateViewModel.tasksTemplate.collect{
//                Log.d("MainActivity", "begin collect")
//                for(item in it)
//                    Log.d("MainActivity", item.toString())
//            }
        }

        lifecycleScope.launch {
            return@launch
//            delay(2000)
            Log.d("MainActivity", "insert")
            //Insert Test
            for (i in 1..2) {
                delay(2000)
                val task = TaskDB(0, "task $i")
                val dao = AppDatabase.getInstance(applicationContext).mainDao()
                val idTask = dao.insertTask(task)
//                dao.insertRunTask(RunTask(0, idTask))
//                for (triggerType in TriggerType.values()) {
//                    val point: Point = Point(0, idTask, "name ${triggerType.name}", triggerType.ordinal.toLong() + 1L, triggerType)
//                    val idPoint = dao.insertPoint(point)
//                    val runPoint = RunPoint(0, idTask, idPoint, dateEnd = Date())
//                    dao.insertRunPoint(runPoint)
////                    delay(1000)
//                    Log.d("MainActivity", "create $runPoint")
//                }
            }

            //Test get
//            dao.runTaskById(1L)?.let {
//                for(item in it.listRunPoint)
//                    Log.d("MainActivity", item.toString())
//            }

        }

    }
}