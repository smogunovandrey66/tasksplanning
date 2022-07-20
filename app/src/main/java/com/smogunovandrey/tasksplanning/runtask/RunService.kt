package com.smogunovandrey.tasksplanning.runtask

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.MainDao
import com.smogunovandrey.tasksplanning.utils.toList
import kotlinx.coroutines.*

data class RunTaskNotification(
    var idRunTask: Long = 0L,
    var idTask: Long = 0L,
    var nameTask: String = "",
    var countPoints: Int = 0,
    var curNumPoint: Long = 1L,
    var idNotification: Int = 1,
    var canDelete: Boolean = true
)

class RunService : Service() {

    private val managerActiveTask by lazy {
        ManagerActiveTask.getInstance(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(ManagerActiveTask.CHANNEL_ID, ManagerActiveTask.CHANNEL_NAME)
        startForeground(ManagerActiveTask.NOTIFICATION_ID,
            managerActiveTask.notificationBuilder(ManagerActiveTask.COMMAND_NEXT).build()
        )
    }

    private fun createNotificationChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                channelId,
                channelName, NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("TasksTemplateFragment", "RunService onStartCommand intent=${intent.extras.toList()}")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TasksTemplateFragment", "Destroy")
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}