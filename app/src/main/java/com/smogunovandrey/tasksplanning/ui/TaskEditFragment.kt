package com.smogunovandrey.tasksplanning.ui

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
}

