package com.smogunovandrey.tasksplanning.taskstemplate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.databinding.FragmentTasksTemplateBinding
import com.smogunovandrey.tasksplanning.runtask.ManagerActiveTask
import com.smogunovandrey.tasksplanning.runtask.RunTaskViewModel
import kotlinx.coroutines.launch

class TasksTemplateFragment : Fragment(), OnRunTaskItemClick {

    private val binding: FragmentTasksTemplateBinding by lazy {
        FragmentTasksTemplateBinding.inflate(layoutInflater)
    }

    private val adapter: AdapterTasksTemplate by lazy{
        AdapterTasksTemplate().apply {
            onRunTaskItemClick = this@TasksTemplateFragment
        }
    }
    private val managerActiveTask by lazy {
        ManagerActiveTask.getInstance(requireContext().applicationContext)
    }

    private val model: TaskTemplateViewModel by activityViewModels()
    private val modelRunTask: RunTaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvTasks.adapter = adapter

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                model.tasksTemplate.collect{ listTasks ->
                    adapter.submitList(listTasks)
                }
            }
        }

        binding.btnAdd.setOnClickListener {
            model.editedTaskWithPoints.clear()
            val action = TasksTemplateFragmentDirections.actionTasksTemplateFragmentToTaskEditFragment(0)
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onRunTaskItemClick(idTask: Long) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                managerActiveTask.startTask(idTask)
                findNavController().navigate(R.id.runTaskActiveFragment)
            }
        }
    }
}