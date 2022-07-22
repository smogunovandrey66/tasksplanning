package com.smogunovandrey.tasksplanning.statistics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.smogunovandrey.tasksplanning.databinding.FragmentStatisticsBinding
import kotlinx.coroutines.launch

class StatisticsFragment: Fragment() {
    private val binding by lazy{
        FragmentStatisticsBinding.inflate(layoutInflater)
    }

    private val model: StatisticsViewModel by activityViewModels()

    private val args by navArgs<StatisticsFragmentArgs>()

    private val adapter by lazy{
        StatisticsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        model.loadStatistics(args.idTask)
        binding.rvStatistics.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner

        lifecycleScope.launch {
            model.statisticPoints.collect{ listStatisticsPointItems ->
                adapter.submitList(listStatisticsPointItems)
                Log.d("StatisticsFragment", "statistics collect=$listStatisticsPointItems")
            }
        }
        lifecycleScope.launch {
            model.taskFlow.collect{task ->
                binding.task = task
            }
        }

        return binding.root
    }
}