package com.smogunovandrey.tasksplanning.taskview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.databinding.FragmentTaskViewBinding
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel
import com.smogunovandrey.tasksplanning.taskstemplate.TaskWithPoints
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TaskViewFragment: Fragment() {

    private val binding: FragmentTaskViewBinding by lazy{
        FragmentTaskViewBinding.inflate(layoutInflater)
    }

    private val adapter: AdapterViewPoints by lazy{
        AdapterViewPoints(emptyList())
    }

    private val args by navArgs<TaskViewFragmentArgs>()
    private val model: TaskTemplateViewModel by activityViewModels()
    private var taskWithPoints = TaskWithPoints()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val id = args.idTask
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvPoints.adapter = adapter


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                model.taskWithPoints(args.idTask).collect{
                    taskWithPoints = it
                    binding.taskItem = it.task
                    adapter.points = it.points
                    adapter.notifyDataSetChanged()
                }
            }
        }

        binding.btnEdit.setOnClickListener {
            val action = TaskViewFragmentDirections.actionTaskViewFragmentToTaskEditFragment(args.idTask)
            model.editedTaskWithPoints = taskWithPoints.deepCopy()
            model.editClickPoint = false
            findNavController().navigate(action)
        }

        return binding.root
    }
}

