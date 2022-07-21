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
import androidx.navigation.fragment.findNavController
import com.smogunovandrey.tasksplanning.databinding.FragmentRunTaskActiveBinding
import com.smogunovandrey.tasksplanning.databinding.FragmentRunTaskViewBinding
import com.smogunovandrey.tasksplanning.taskstemplate.RunTaskWithPoints
import com.smogunovandrey.tasksplanning.taskstemplate.TasksTemplateFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private val model: RunTaskViewModel by activityViewModels()

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
                    if(it == null){
                        findNavController().popBackStack()

                        if(managerActiveTask.idRunTask > 0L)
                            findNavController().navigate(TasksTemplateFragmentDirections.actionTasksTemplateFragmentToRunTaskViewFragment(managerActiveTask.idRunTask))
                    } else {
                        binding.runTaskWithPoints = it
                        managerActiveTask.idRunTask = it.runTask.idRunTask
                        adapter.submitList(it.points)
                    }
                }
            }
        }

        binding.btnNext.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.Default) {
                    managerActiveTask.markPoint()
                }
            }
        }

        binding.btnCancel.setOnClickListener {
//            findNavController().popBackStack()
            managerActiveTask.idRunTask = 0L
            lifecycleScope.launch {
                managerActiveTask.cancelTask()
            }
        }

        return binding.root
    }
}