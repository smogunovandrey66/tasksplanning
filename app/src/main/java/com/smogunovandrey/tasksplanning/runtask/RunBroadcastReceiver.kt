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
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.MainDao
import com.smogunovandrey.tasksplanning.utils.toTask
import kotlinx.coroutines.*


class RunTaskWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context,
    workerParams
){
    override suspend fun doWork(): Result {
        return Result.success()
    }
}
class RunBroadcastReceiver: BroadcastReceiver() {

    init {
        Log.d("RunBroadcastReceiver", "init")
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("RunBroadcastReceiver", "onReceive,context=${context.toString()}")
        val managerActiveTask = ManagerActiveTask.getInstance(context)
        val task = managerActiveTask.activeRunTaskWithPointsFlow.value
        if(task != null){
            val coroutineScope = CoroutineScope(Dispatchers.Default)
            coroutineScope.launch {
                when(intent.getIntExtra(ManagerActiveTask.COMMAND_ID, 0)) {
                    ManagerActiveTask.COMMAND_NEXT -> managerActiveTask.markPoint()
                    ManagerActiveTask.COMMAND_CANCEL -> {
                        managerActiveTask.idRunTask = 0L
                        managerActiveTask.cancelTask()
                    }
                }
                cancel()
            }
        }
    }
}