package com.smogunovandrey.tasksplanning.runtask

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
import com.smogunovandrey.tasksplanning.databinding.FragmentRunTaskActiveBinding
import com.smogunovandrey.tasksplanning.databinding.FragmentRunTaskViewBinding
import com.smogunovandrey.tasksplanning.taskstemplate.RunTaskWithPoints
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RunTaskActiveFragment : Fragment() {
    private val binding by lazy {
        FragmentRunTaskActiveBinding.inflate(layoutInflater)
    }

    private val adapter by lazy {
        AdapterRunPoints()
    }

    private val managerActiveTask by lazy {
        ManagerActiveTask.getInstance(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvRunPoints.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                managerActiveTask.activeRunTaskWithPointsFlow.collect{
                    Log.d("RunTaskViewFragment", "$it")
                }
            }
        }

        return binding.root
    }
}