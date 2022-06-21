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
import androidx.navigation.fragment.navArgs
import com.smogunovandrey.tasksplanning.databinding.FragmentTaskViewBinding
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel
import kotlinx.coroutines.launch

class TaskViewFragment: Fragment() {

    private val binding: FragmentTaskViewBinding by lazy{
        FragmentTaskViewBinding.inflate(layoutInflater)
    }

    private val adapter: AdapterPoints by lazy{
        AdapterPoints(emptyList())
    }

    private val args by navArgs<TaskViewFragmentArgs>()
    private val model: TaskTemplateViewModel by activityViewModels()

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
                model.pointsTemplate(args.idTask).collect{
                    adapter.points = it
                    adapter.notifyDataSetChanged()
                }
            }
        }

        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                model.taskById(args.idTask).collect{

                    it?.let {
                        binding.taskItem = it
                    }
                }
            }
        }

        return binding.root
    }
}

