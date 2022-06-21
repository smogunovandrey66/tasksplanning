package com.smogunovandrey.tasksplanning.taskedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smogunovandrey.tasksplanning.databinding.FragmentTaskEditBinding

class TaskEditFragment: Fragment() {

    private val binding by lazy {
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