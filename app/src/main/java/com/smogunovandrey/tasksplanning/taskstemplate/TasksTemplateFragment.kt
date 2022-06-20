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
import com.smogunovandrey.tasksplanning.databinding.FragmentTasksTemplateBinding
import com.smogunovandrey.tasksplanning.viewmodel.TaskTemplateViewModel
import kotlinx.coroutines.launch

class TasksTemplateFragment : Fragment() {

    private val binding: FragmentTasksTemplateBinding by lazy {
        FragmentTasksTemplateBinding.inflate(layoutInflater)
    }

    private val adapter: AdapterTasksTemplate by lazy{
        AdapterTasksTemplate()
    }

    private val taskTemplateViewModel: TaskTemplateViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvTasks.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                taskTemplateViewModel.tasksTemplate.collect{ listTasks ->
                    val listAdapter = mutableListOf<TaskItem>()
                    listTasks.map {
                        val taskItem = TaskItem(it.task.idTask, it.task.name)
                        it.listTaskPoint.map{ point ->
                            taskItem.points.add(PointItem(point.idPoint, point.name, point.num, point.triggerType))
                        }
                        listAdapter.add(taskItem)
                    }
                    adapter.submitList(listAdapter)
                }
            }
        }



        return binding.root
    }
}