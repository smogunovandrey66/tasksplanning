package com.smogunovandrey.tasksplanning.runtask

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.MainDao
import com.smogunovandrey.tasksplanning.utils.toTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class RunBroadcastReceiver: BroadcastReceiver() {

    private fun dao(context: Context) = AppDatabase.getInstance(context).mainDao()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        Log.d("RunBroadcastReceiver", "init")
    }

    private fun createNotification(context: Context) {

        val layoutNotification = RemoteViews(context.packageName, R.layout.notification_run_task)
        layoutNotification.setImageViewResource(R.id.btn_next, R.drawable.baseline_add_24)
        val notificationBuilder = NotificationCompat.Builder(
            context.applicationContext, CHANNEL_ID
        )
            .setCustomContentView(layoutNotification)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(R.drawable.baseline_run_circle_24)
            .setOngoing(true)

        val notifyManager =
            context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notifyManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setSmallIcon(R.drawable.baseline_add_24).build()
        )
    }


    override fun onReceive(context: Context, intent: Intent) {
        Log.d("RunBroadcastReceiver", "onReceive,context=${context.toString()}")
        val idTask = intent.getLongExtra("idTask", 0L)
        if (idTask > 0L) {
            coroutineScope.launch {
                val task = dao(context).taskByIdSuspend(idTask).toTask()

                createNotification(context)

                cancel()
            }
        }
    }

    companion object{
        val CHANNEL_ID = "id channel1"
        val CHANNEL_NAME = "name channel1"
        val NOTIFICATION_ID = 1
    }
}