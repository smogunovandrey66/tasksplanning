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
import androidx.navigation.fragment.navArgs
import com.smogunovandrey.tasksplanning.databinding.FragmentRunTaskViewBinding
import com.smogunovandrey.tasksplanning.taskstemplate.RunTaskWithPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RunTaskViewFragment : Fragment() {
    private val binding by lazy {
        FragmentRunTaskViewBinding.inflate(layoutInflater)
    }

    private val adapter by lazy {
        AdapterRunPoints()
    }

    private val args: RunTaskViewFragmentArgs by navArgs()

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
                model.loadRunTask(args.idRunTask)
                model.curRunTaskWithPoints.collect{
                    adapter.submitList(it.points)
                    binding.runTaskWithPoints = it
                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}