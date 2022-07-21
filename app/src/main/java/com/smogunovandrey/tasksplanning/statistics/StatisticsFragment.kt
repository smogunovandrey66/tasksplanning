package com.smogunovandrey.tasksplanning.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.smogunovandrey.tasksplanning.databinding.FragmentStatisticsBinding

class StatisticsFragment: Fragment() {
    private val binding by lazy{
        FragmentStatisticsBinding.inflate(layoutInflater)
    }

    private val model: StatisticsViewModel by activityViewModels()

    private val args by navArgs<StatisticsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        model.loadStatistics(args.idTask)
        return binding.root
    }
}