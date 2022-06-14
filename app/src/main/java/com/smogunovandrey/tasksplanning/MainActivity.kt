package com.smogunovandrey.tasksplanning

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.Point
import com.smogunovandrey.tasksplanning.db.TriggerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "tasks.db").build()
        scope.launch {
        db.mainDao().allTaskWithPoint()

//            for(triggerType in TriggerType.values()){
//                val point: Point = Point(0, "name ${triggerType.name}", triggerType)
//                AppDatabase.getInstance(applicationContext).mainDao().insertPoint(point)
//            }
        }

    }
}