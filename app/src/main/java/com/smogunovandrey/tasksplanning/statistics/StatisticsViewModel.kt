package com.smogunovandrey.tasksplanning.statistics

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.smogunovandrey.tasksplanning.db.AppDatabase
import com.smogunovandrey.tasksplanning.taskedit.TaskEditFragmentArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class StatisticPointItem(
    var idTask: Long = 0L, //Need whether
    var idPoint: Long = 0L,
    var name: String = "",
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

    private val statisticPoints: StateFlow<List<StatisticPointItem>> = _statisticPoints

    var idTask: Long = 0

    fun loadStatistics(idTask: Long){
        viewModelScope.launch {
            val tasksDB = withContext(Dispatchers.Default){
                dao.runTasksByIdTaskSuspend(idTask)
            }
            Log.d("StatisticsViewModel", "tasksDB=$tasksDB")
            this@StatisticsViewModel.idTask = idTask
        }
    }
}