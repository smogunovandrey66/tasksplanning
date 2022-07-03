package com.smogunovandrey.tasksplanning.runtask

import android.content.Intent
import android.os.Build
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
import androidx.navigation.fragment.navArgs
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.databinding.FragmentRunTaskViewBinding
import kotlinx.coroutines.launch

class RunTaskViewFragment: Fragment() {
    private val binding by lazy{
        FragmentRunTaskViewBinding.inflate(layoutInflater)
    }
    private val model: RunTaskViewModel by activityViewModels()
    private val args: RunTaskViewFragmentArgs by navArgs()
    private val adapter by lazy {
        AdapterRunPoints()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvRunPoints.adapter = adapter
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                model.loadRunTask(args.idTask, args.idRunTask)
                model.loadedRunTaskWithPoints.collect{
                    Log.d("AdapterRunPoints", "collect")
                    binding.task = it.runTask
                    adapter.submitList(it.points)

                    if(it.runTask.id == 0L)
                        binding.btnStart.text = getString(R.string.start)
                }
            }
        }

        binding.btnStart.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireActivity().startForegroundService(Intent())
            }
        }

        return binding.root
    }
}