package com.smogunovandrey.tasksplanning

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.smogunovandrey.tasksplanning.databinding.ActivityMainBinding
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.PointDB
import com.smogunovandrey.tasksplanning.db.TaskDB
import com.smogunovandrey.tasksplanning.db.TriggerType
import com.smogunovandrey.tasksplanning.runtask.ManagerActiveTask
import com.smogunovandrey.tasksplanning.runtask.RunService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val managerActiveTask by lazy {
        ManagerActiveTask.getInstance(applicationContext)
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        //Cause use FragmentContainerView(not <fragment>) need supportFragmentManager
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        //For test first time if database is empty
        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(applicationContext).mainDao()
            val tasks = dao.allTasksSuspend()
            if (tasks.isEmpty()) {
                var taskDB = TaskDB(0, "Morning")
                var idTask = dao.insertTask(taskDB)
                dao.insertPoint(PointDB(0, idTask, "Lift", 1, TriggerType.HAND))
                dao.insertPoint(PointDB(0, idTask, "5-ka", 2, TriggerType.HAND))
                dao.insertPoint(PointDB(0, idTask, "Bus", 3, TriggerType.HAND))

                taskDB = TaskDB(0, "Work")
                idTask = dao.insertTask(taskDB)
                dao.insertPoint(PointDB(0, idTask, "Parkoviy", 1, TriggerType.HAND))
                dao.insertPoint(PointDB(0, idTask, "Goznak", 2, TriggerType.HAND))
                dao.insertPoint(PointDB(0, idTask, "Work", 3, TriggerType.HAND))
            }
        }

        lifecycleScope.launch {
            withContext(Dispatchers.Default) {
                managerActiveTask.reloadActiviTask()
            }
            if (managerActiveTask.activeRunTaskWithPointsFlow.value != null) {
                //Start Foreground Service
                ContextCompat.startForegroundService(applicationContext,
                    Intent(applicationContext, RunService::class.java).apply {
                        putExtra(ManagerActiveTask.COMMAND_ID, ManagerActiveTask.COMMAND_NEXT)
                    }
                )
            } else {
                stopService(Intent(applicationContext, RunService::class.java))
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}