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
import com.smogunovandrey.tasksplanning.databinding.FragmentRunTaskViewBinding
import com.smogunovandrey.tasksplanning.taskstemplate.RunTaskWithPoints
import kotlinx.coroutines.launch

class RunTaskViewFragment : Fragment() {
    private val binding by lazy {
        FragmentRunTaskViewBinding.inflate(layoutInflater)
    }
    private val model: RunTaskViewModel by activityViewModels()
    private val adapter by lazy {
        AdapterRunPoints()
    }
    private lateinit var runTaskWithPoints: RunTaskWithPoints

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvRunPoints.adapter = adapter
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.updateCurRunTask()
                model.curRunTaskWithPoints.collect {
                    Log.d("AdapterRunPoints", "collect $it")
                    runTaskWithPoints = it
                    binding.runTaskWithPoints = it
                    adapter.submitList(it.points)
                }
            }
        }

        binding.btnStart.setOnClickListener {
            if(model.curIdRunTask == 0L)
                model.runTask(runTaskWithPoints)
            else
                model.nextPointHand()
        }

        return binding.root
    }
}