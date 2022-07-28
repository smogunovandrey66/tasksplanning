package com.smogunovandrey.tasksplanning.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.smogunovandrey.tasksplanning.databinding.FragmentMapBinding
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.CameraPosition

class MapFragment: Fragment() {
    private val binding by lazy {
        FragmentMapBinding.inflate(layoutInflater)
    }

    private val model: TaskTemplateViewModel by activityViewModels()
    private val gpsPoint: com.yandex.mapkit.geometry.Point? by lazy {
        model.editedPoint.gpsPoint
    }
    private val mapView by lazy {
        binding.mvMain
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gpsPoint?.let {
            mapView.map.move(CameraPosition(it, 14.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 5.0f),
                null
            )
        }


        return binding.root
    }


    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}