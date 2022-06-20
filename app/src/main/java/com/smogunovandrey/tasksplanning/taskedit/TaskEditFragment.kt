package com.smogunovandrey.tasksplanning.taskedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smogunovandrey.tasksplanning.databinding.FragmentTaskEditBinding

class TaskEditFragment: Fragment() {

    private val binding: FragmentTaskEditBinding by lazy{
        FragmentTaskEditBinding.inflate(layoutInflater)
    }

    private val adapter: AdapterPoints by lazy{
        AdapterPoints()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvPoints.adapter = adapter
        return binding.root
    }
}

