package com.smogunovandrey.tasksplanning.runtask

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
import com.smogunovandrey.tasksplanning.databinding.FragmentRunTaskViewBinding
import kotlinx.coroutines.launch

class RunTaskViewFragment: Fragment() {
    private val binding by lazy{
        FragmentRunTaskViewBinding.inflate(layoutInflater)
    }
    private val model: RunTaskViewModel by activityViewModels()
    private val args: RunTaskViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){

            }
        }

        return binding.root
    }
}