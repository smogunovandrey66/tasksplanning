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

    private val dao: MainDao by lazy {
        AppDatabase.getInstance(this).mainDao()
    }
    private val coroutine: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(CHANNEL_ID, CHANNEL_NAME)
        val layoutRemoteViews = RemoteViews(packageName, R.layout.notification_run_task)
        layoutRemoteViews.setImageViewResource(R.id.btn_next, R.drawable.baseline_play_arrow_24)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setCustomContentView(layoutRemoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(R.drawable.baseline_run_circle_24)
        startForeground(ID_NOTIFICATION_INT, notificationBuilder.build())
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

    private fun updateNotification(intent: Intent) {
        val command = intent.getIntExtra(COMMAND, 0)

        if (command == COMMAND_STOP) {
            stopSelf()
            return
        }

        val idRunTask = intent.getLongExtra(ID_RUN_TASK, 0)
        val nameTask = intent.getStringExtra(NAME_TASK) ?: ""
        var curNumPoint = intent.getLongExtra(CUR_NUM_POINT, 0L)
        val countPoints = intent.getIntExtra(COUNT_POINTS, 0)
        var idNotification: Int = ID_NOTIFICATION_INT

        if (curNumPoint >= countPoints) {
            stopSelf()
            return
        }

        val layoutRemoteViews = RemoteViews(packageName, R.layout.notification_run_task)
        layoutRemoteViews.setTextViewText(R.id.txt_name, nameTask.toString())
        if(curNumPoint > 0L)
            layoutRemoteViews.setTextViewText(R.id.txt_number, curNumPoint.toString())

        val intentNext = Intent(this.applicationContext, RunService::class.java)
            .apply { action = "intentNext_$idNotification" }
        intentNext.putExtra(ID_NOTIFICATION, idNotification)
        intentNext.putExtra(ID_RUN_TASK, idRunTask)
        intentNext.putExtra(NAME_TASK, nameTask)
        intentNext.putExtra(CUR_NUM_POINT, curNumPoint)
        intentNext.putExtra(COUNT_POINTS, countPoints)
        intentNext.putExtra(COMMAND, COMMAND_NEXT)

        val pendingIntentNext =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) PendingIntent.getForegroundService(
                applicationContext,
                1,
                intentNext,
                PendingIntent.FLAG_IMMUTABLE + PendingIntent.FLAG_UPDATE_CURRENT
            ) else PendingIntent.getService(
                applicationContext,
                1,
                intentNext,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        if(curNumPoint == 0L)
            layoutRemoteViews.setImageViewResource(R.id.btn_next, R.drawable.baseline_play_arrow_24)
        layoutRemoteViews.setOnClickPendingIntent(R.id.btn_next, pendingIntentNext)
        val intentStop = Intent(this.applicationContext, RunService::class.java)
            .apply { action = "intentStop_$idNotification" }
        intentStop.putExtra(ID_NOTIFICATION, idNotification)
        intentStop.putExtra(ID_RUN_TASK, idRunTask)
        intentStop.putExtra(NAME_TASK, nameTask)
        intentStop.putExtra(CUR_NUM_POINT, curNumPoint)
        intentStop.putExtra(COUNT_POINTS, countPoints)
        intentStop.putExtra(COMMAND, COMMAND_STOP)

        val pendingIntentStop =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) PendingIntent.getForegroundService(
                applicationContext,
                1, intentStop,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            ) else PendingIntent.getService(
                applicationContext,
                1,
                intentStop,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        layoutRemoteViews.setOnClickPendingIntent(R.id.btn_stop, pendingIntentStop)


        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setCustomContentView(layoutRemoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(R.drawable.baseline_run_circle_24)
        notificationManager.notify(ID_NOTIFICATION_INT, notificationBuilder.build())
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("RunService", "onStartCommand intent=${intent.extras.toList()}")
        updateNotification(intent)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutine.cancel()
        Log.d("RunService", "Destroy")
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        const val CHANNEL_ID = "id channel1"
        const val CHANNEL_NAME = "name channel1"

        const val COMMAND = "command"
        const val COMMAND_START = 1
        const val COMMAND_NEXT = COMMAND_START + 1
        const val COMMAND_STOP = COMMAND_START + 2

        const val ID_NOTIFICATION_INT = 1

        const val TASK_ID = "id task"
        const val ID_NOTIFICATION = "id notification"
        const val ID_RUN_TASK = "id_run_task"
        const val NAME_TASK = "name task"
        const val CUR_NUM_POINT = "current number point"
        const val COUNT_POINTS = "count points"

        fun runTask(context: Context, runTaskNotification: RunTaskNotification, command: Int) {
            val intent = Intent(context, RunService::class.java)
            intent.putExtra(COMMAND, command)
            intent.putExtra(ID_RUN_TASK, runTaskNotification.idRunTask)
            intent.putExtra(NAME_TASK, runTaskNotification.nameTask)
            intent.putExtra(CUR_NUM_POINT, runTaskNotification.curNumPoint)
            intent.putExtra(COUNT_POINTS, runTaskNotification.countPoints)
            Log.d("RunService", "in fun runTask = $runTaskNotification")
            ContextCompat.startForegroundService(context, intent)
        }
    }
}