package com.smogunovandrey.tasksplanning.map

import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.databinding.FragmentMapBinding
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.runtime.image.ImageProvider
import kotlin.math.log

class MapFragment : Fragment(), InputListener {
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

    private val map by lazy {
        mapView.map
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("MapFragment", "onCreateView model=$model")
        gpsPoint?.let {
            mapView.map.move(
                CameraPosition(it, 14.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1.0f),
                null
            )

            map.mapObjects.addPlacemark(
                it,
                ImageProvider.fromResource(requireContext(), R.drawable.mark)
            )

//            map.mapObjects.addPlacemark(it).apply {
//                setIcon(ImageProvider.fromResource(this@MapFragment.requireActivity(), R.drawable.mark))
//            }
        }

//        map.addTapListener{geoObject ->
//            Log.d("MapFragment", "tap geoObjec=$geoObject")
//            false
//        }
//        map.addInputListener(object: InputListener {
//            override fun onMapTap(mp: Map, pnt: Point) {
//                            map.mapObjects.addPlacemark(pnt).apply {
//                setIcon(ImageProvider.fromResource(this@MapFragment.requireActivity(), R.drawable.mark))
//            }
//
//
////                map.deselectGeoObject()
//                Log.d("MapFragment", "input Listener onMapTap mpa=$mp, point=$pnt")
//            }
//
//            override fun onMapLongTap(mp: Map, pnt: Point) {
//                Log.d("MapFragment", "input Listener onMapLongTap mpa=$mp, point=$pnt")
//            }
//
//        })
//        map.mapObjects.addTapListener{mapObject, point ->
//            map.deselectGeoObject()
//            Log.d("MapFragment", "tap mapObject=$mapObject, point=$point")
//            true
//        }

        map.addInputListener(this)

        return binding.root
    }


    override fun onStart() {
        Log.d("MapFragment", "onStart begin")
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
        Log.d("MapFragment", "onStart end")
    }

    override fun onStop() {
        Log.d("MapFragment", "onStop begin")
        map.removeInputListener(this)
        super.onStop()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        Log.d("MapFragment", "onStop end")
    }

    override fun onMapTap(m: Map, p: Point) {
        map.mapObjects.clear()
        map.mapObjects.addPlacemark(
            p,
            ImageProvider.fromResource(requireContext(), R.drawable.mark)
        )
        model.editedPoint.gpsPoint = Point(p.latitude, p.longitude)
        Log.d("MapFragment", "onMapTap map=$m,point=$p")
    }

    override fun onMapLongTap(m: Map, p: Point) {
        Log.d("MapFragment", "onMapLongTap map=$m,point=$p")
    }
}