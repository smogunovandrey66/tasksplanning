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
//                delay(2000)
                val taskDB = TaskDB(0, "task $i")
                val dao = AppDatabase.getInstance(applicationContext).mainDao()
                val idTask = dao.insertTask(taskDB)

//                dao.insertRunTask(RunTask(0, idTask))
                for (triggerType in TriggerType.values()) {
                    val pointDb = PointDB(0, idTask, "name ${triggerType.name}", triggerType.ordinal + 1L, triggerType)
                    val idPoint = dao.insertPoint(pointDb)
                }
            }

            //Test get
//            dao.runTaskById(1L)?.let {
//                for(item in it.listRunPoint)
//                    Log.d("MainActivity", item.toString())
//            }

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}