package com.smogunovandrey.tasksplanning.statistics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.smogunovandrey.tasksplanning.databinding.FragmentStatisticsBinding
import kotlinx.coroutines.launch

class StatisticsFragment: Fragment() {
    private val binding by lazy{
        FragmentStatisticsBinding.inflate(layoutInflater)
    }

    private val model: StatisticsViewModel by activityViewModels()

    private val args by navArgs<StatisticsFragmentArgs>()

    private val statisticsAdapter by lazy{
        StatisticsAdapter()
    }

    private val runTasksAdapter by lazy{
        RunTasksAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model.loadStatistics(args.idTask)
        binding.rvStatistics.adapter = statisticsAdapter
        binding.rvRunTasks.adapter = runTasksAdapter
        binding.lifecycleOwner = viewLifecycleOwner

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                model.statisticPoints.collect{ listStatisticsPointItems ->
                    statisticsAdapter.submitList(listStatisticsPointItems)
                    Log.d("StatisticsFragment", "statistics collect=$listStatisticsPointItems")
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    model.taskFlow.collect{task ->
                        binding.task = task
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    model.runTasks.collect{
                        Log.d("StatisticsFragment", "model.runTasks.collect=$it")
                        runTasksAdapter.submitList(it)
                    }
                }
            }
        }

        return binding.root
    }
}