package com.smogunovandrey.tasksplanning

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.smogunovandrey.tasksplanning.databinding.ActivityMainBinding
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.PointDB
import com.smogunovandrey.tasksplanning.db.TaskDB
import com.smogunovandrey.tasksplanning.db.TriggerType
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        //Cause use FragmentContainerView(not <fragment>) need supportFragmentManager
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController =  navHost.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        //For test first time if database is empty
        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(applicationContext).mainDao()
            val tasks = dao.allTasksSuspend()
            if(tasks.isEmpty()){
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

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}