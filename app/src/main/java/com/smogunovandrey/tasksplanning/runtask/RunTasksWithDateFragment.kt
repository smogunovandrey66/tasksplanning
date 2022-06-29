package com.smogunovandrey.tasksplanning.runtask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smogunovandrey.tasksplanning.databinding.FragmentRunTasksWithDateBinding

class RunTasksWithDateFragment: Fragment() {
    private val binding by lazy{
        FragmentRunTasksWithDateBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
}