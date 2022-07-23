package com.smogunovandrey.tasksplanning.statistics

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.taskstemplate.RunTask
import com.smogunovandrey.tasksplanning.taskstemplate.Task
import com.smogunovandrey.tasksplanning.utils.toTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.DirectoryStream

data class StatisticPointItem(
    var idTask: Long = 0L, //Need whether
    var idPoint: Long = 0L,
    var namePoint: String = "",
    var numPoint: Int = 0,
    var minDuration: Long = 0L,
    var averageDuration: Long = 0L,
    var maxDuration: Long = 0L
)

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {
    private val _statisticPoints: MutableStateFlow<MutableList<StatisticPointItem>> =
        MutableStateFlow(
            mutableListOf()
        )

    private val dao by lazy {
        AppDatabase.getInstance(application.applicationContext).mainDao()
    }

    val statisticPoints: StateFlow<List<StatisticPointItem>> = _statisticPoints

    var idTask: Long = 0

    private val _taskFlow: MutableStateFlow<Task> = MutableStateFlow(Task())
    val taskFlow: StateFlow<Task> = _taskFlow

    private val _runTasks: MutableStateFlow<List<RunTask>> = MutableStateFlow(mutableListOf())
    val runTasks: StateFlow<List<RunTask>> = _runTasks

    fun loadStatistics(idTask: Long) {
        viewModelScope.launch {
            val res: MutableList<StatisticPointItem> = mutableListOf()
            val numDurations: MutableMap<Int, MutableList<Long>> = mutableMapOf()

            //Template
            val taskWithPoints = withContext(Dispatchers.Default) {
                dao.taskWithPointsSuspend(idTask)
            }
            withContext(Dispatchers.Default){
                _taskFlow.emit(dao.taskByIdSuspend(idTask).toTask())
            }
            val taskDB = taskWithPoints.task
            val sortedPointsDB = taskWithPoints.points.sortedWith { p1, p2 ->
                p1.num - p2.num
            }

            //All run points
            val runTasksWithPointsDB = withContext(Dispatchers.Default) {
                dao.runTasksByIdTaskSuspend(idTask)
            }

            runTasksWithPointsDB.map {
                RunTask(
                    idRunTask = it.runTask.idRunTask,
                    dateCreate = it.runTask.dateCreate
                )
            }.apply {
                Log.d("StatisticsFragment", "_runTasks.emit $this")
                _runTasks.emit(this)
            }

            Log.d("StatisticsFragment", "runTasksWithPointsDB=$runTasksWithPointsDB")

            for (runTaskWithPointsDB in runTasksWithPointsDB) {
                val lastIdx = runTaskWithPointsDB.runPoints.size - 1
                val sortedRunPoints = runTaskWithPointsDB.runPoints.sortedWith { p1, p2 -> p1.num - p2.num }
                for ((curIdx, runPoint) in sortedRunPoints.withIndex()) {
                    //Prev datetime in milliseconds
                    var prevDateMilliseconds = 0L

                    //second point and later
                    if(curIdx > 0){
                        sortedRunPoints[curIdx - 1].dateMark?.let {
                            prevDateMilliseconds = it.time
                        }
                    } else {
                        //first point - datetime from dateCreate of RunTask
                        prevDateMilliseconds = runTaskWithPointsDB.runTask.dateCreate.time
                    }

                    sortedRunPoints[curIdx].dateMark?.let {
                        numDurations.getOrPut(runPoint.num) { mutableListOf() }.add(it.time - prevDateMilliseconds)
                    }
                }
            }

            for (pointDB in sortedPointsDB) {
                res.add(
                    StatisticPointItem(
                        taskDB.idTask,
                        pointDB.idPoint,
                        pointDB.name,
                        pointDB.num,
                        numDurations[pointDB.num]?.minOf { value -> value } ?: 0L,
                        numDurations[pointDB.num]?.average()?.toLong() ?: 0L,
                        numDurations[pointDB.num]?.maxOf { value -> value } ?: 0L
                    )
                )
            }

            Log.d("StatisticsFragment", "tasksDB=$runTasksWithPointsDB")
            this@StatisticsViewModel.idTask = idTask
            _statisticPoints.emit(res)
        }
    }
}