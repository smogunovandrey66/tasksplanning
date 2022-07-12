package com.smogunovandrey.tasksplanning.runtask

import android.app.Application
import android.content.Context
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.db.RunTaskWithPointDB
import com.smogunovandrey.tasksplanning.taskstemplate.RunTaskWithPoints

class ManagerActiveTask(val context: Context) {
    private val dao by lazy {
        AppDatabase.getInstance(context).mainDao()
    }

    suspend fun activiTask(): RunTaskWithPoints?{
        val runTaskWithPointsDB: RunTaskWithPointDB? = dao.activeRunTaskWithPointsSuspend()

        val runTaskWithPoints: RunTaskWithPoints? = RunTaskWithPoints()
        return runTaskWithPoints
    }
}