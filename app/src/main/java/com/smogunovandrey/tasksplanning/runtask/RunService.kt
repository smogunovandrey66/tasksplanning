package com.smogunovandrey.tasksplanning.runtask

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.smogunovandrey.tasksplanning.R
import kotlinx.coroutines.coroutineScope

class RunService: Service() {
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(CHANNEL_ID[NOTIFICATION_ID],
                CHANNEL_NAME[NOTIFICATION_ID], NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("RunService", "On create")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID[NOTIFICATION_ID])
            .setContentTitle(CHANNEL_NAME[NOTIFICATION_ID])
            .setContentText(CHANNEL_ID[NOTIFICATION_ID])
            .setSmallIcon(if(NOTIFICATION_ID == 1) R.drawable.baseline_run_circle_24
            else R.drawable.baseline_edit_24)
            .build()
        startForeground(NOTIFICATION_ID, notification)
        Log.d("RunService", "onStartCommand startId=$startId, NOTIFICATION_ID=$NOTIFICATION_ID")
        NOTIFICATION_ID++
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("RunService", "Destroy")
    }



    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object{
        val CHANNEL_ID = arrayOf("id channel1", "id channdel 2", "id channel 3", "id channdel 4")
        val CHANNEL_NAME = arrayOf("name channel1", "name channel 2", "name channel 3", "name channel4")
        var NOTIFICATION_ID = 1
    }
}