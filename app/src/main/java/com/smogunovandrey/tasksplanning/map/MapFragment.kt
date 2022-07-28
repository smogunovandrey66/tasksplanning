package com.smogunovandrey.tasksplanning.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.smogunovandrey.tasksplanning.databinding.FragmentMapBinding
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel

class MapFragment: Fragment() {
    private val binding by lazy {
        FragmentMapBinding.inflate(layoutInflater)
    }

    private val model: TaskTemplateViewModel by activityViewModels()
    private val gpsPoint: com.yandex.mapkit.geometry.Point? by lazy {
        model.editedPoint.gpsPoint
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model.editedPoint.gpsPoint

        return binding.root
    }
}