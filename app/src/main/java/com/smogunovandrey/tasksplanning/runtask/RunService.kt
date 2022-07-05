package com.smogunovandrey.tasksplanning.runtask

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaSession2Service.MediaNotification
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RelativeLayout
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.MainDao
import com.smogunovandrey.tasksplanning.db.RunTaskDB
import com.smogunovandrey.tasksplanning.taskstemplate.RunTask
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

data class RunTaskNotification(
    var idRunTask: Long = 0L,
    var idTask: Long = 0L,
    var maxPoints: Int = 0,
    var curNumPoint: Long = 1L,
    var idNotification: Int
)

class RunService : Service() {
    private val dao: MainDao by lazy {
        AppDatabase.getInstance(this).mainDao()
    }
    private val coroutine: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private val notificationIdWithTask = mutableMapOf<Int, RunTaskNotification>()

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

    private fun notificationBuilder(channelId: String): NotificationCompat.Builder {
        val layoutNotification = RemoteViews(packageName, R.layout.notification_run_task)
//        layoutNotification.setImageViewResource(R.id.btn_next, R.drawable.baseline_skip_next_24)

        val intent = Intent(this.applicationContext, RunService::class.java)
        val pendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) PendingIntent.getForegroundService(
                applicationContext,
                1,
                intent,
                PendingIntent.FLAG_ONE_SHOT
            ) else PendingIntent.getService(
                applicationContext,
                1,
                intent,
                PendingIntent.FLAG_ONE_SHOT
            )
        layoutNotification.setOnClickPendingIntent(R.id.btn_next, pendingIntent)

        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("some Title")
            .setCustomContentView(layoutNotification)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(R.drawable.baseline_run_circle_24)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(CHANNEL_ID, CHANNEL_NAME)
        startForeground(NOTIFICATION_ID, notificationBuilder(CHANNEL_ID).build())
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        var nameTask = ""

        coroutine.launch {
            dao.activeTask()?.let {
                val runTask = RunTask(
                    it.runTask.id,
                    it.task.id,
                    it.task.name,
                    it.runTask.dateCreate,
                    it.runTask.active
                )
            }
            delay(3000)
            createNotificationChannel(CHANNEL_ID, CHANNEL_NAME)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(
                NOTIFICATION_ID + 1,
                notificationBuilder(CHANNEL_ID)
                    .setOngoing(true)
                    .build()
            )
//            startForeground(NOTIFICATION_ID + 1, notificationBuilder(CHANNEL_ID + "yet").build())
//            stopSelf()
            delay(4000)
//            notificationManager.cancel(NOTIFICATION_ID + 1)
//            delay(2000)
//            stopSelf()
        }

        val command = intent.getIntExtra(COMMAND, 0)
        when(command){
            COMMAND_START -> {
                val idRunTask = intent.getLongExtra(ID_RUN_TASK, 0)
            }
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

    companion object {
        const val CHANNEL_ID = "id channel1"
        const val CHANNEL_NAME = "name channel1"
        const val NOTIFICATION_ID = 1

        const val COMMAND = "command"
        const val COMMAND_START = 1
        const val COMMAND_NEXT = COMMAND_START + 1
        const val TASK_ID = "id task"
        const val ID_RUN_TASK = "id_run_task"

        fun runTask(context: Context, runTaskNotification: RunTaskNotification){
            val intent = Intent(context, RunService::class.java)
            intent.putExtra(COMMAND, COMMAND_START)
            intent.putExtra(ID_RUN_TASK, runTaskNotification.idRunTask)
            ContextCompat.startForegroundService(context, intent)
        }
    }
}