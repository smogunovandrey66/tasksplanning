package com.smogunovandrey.tasksplanning.runtask

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaSession2Service.MediaNotification
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.MainDao
import com.smogunovandrey.tasksplanning.taskstemplate.RunTask
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class RunService: Service() {
    private val dao: MainDao by lazy {
        AppDatabase.getInstance(this).mainDao()
    }
    private val coroutine: CoroutineScope = CoroutineScope(Dispatchers.Main)

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var nameTask = ""

        createNotificationChannel()
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(getString(R.string.content_title_run_task))
            .setContentText(nameTask)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0))
            .addAction(R.drawable.baseline_play_arrow_24, "t", null)
            .setSmallIcon(R.drawable.baseline_run_circle_24)



        startForeground(NOTIFICATION_ID, notificationBuilder.build())

        coroutine.launch {
            dao.activeTask()?.let {
                val runTask = RunTask(it.runTask.id, it.task.id, it.task.name, it.runTask.dateCreate, it.runTask.active)
            }
            delay(3000)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(NOTIFICATION_ID,
            notificationBuilder.setSmallIcon(R.drawable.baseline_add_24).build())
            delay(2000)
            stopSelf()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutine.cancel()
    }



    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object{
        val CHANNEL_ID = "id channel1"
        val CHANNEL_NAME = "name channel1"
        val NOTIFICATION_ID = 1
    }
}