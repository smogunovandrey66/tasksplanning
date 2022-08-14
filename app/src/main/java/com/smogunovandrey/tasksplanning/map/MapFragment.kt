package com.smogunovandrey.tasksplanning.map

import android.graphics.drawable.Icon
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
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
import com.yandex.mapkit.map.PlacemarkMapObject
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
    private var markerGpsPoinnt: PlacemarkMapObject? = null
    private var markerCurLocation: PlacemarkMapObject? = null

    //***************GPS****************** begin
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private val locationRequest = com.google.android.gms.location.LocationRequest()
        .setInterval(5000L)
        .setFastestInterval(2500L)
        .setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            Log.d("MapFragment", "locationResult=$locationResult")
            if (locationResult != null && locationResult.locations.size > 0) {
//                stopLocationUpdate() need ??

                markerCurLocation?.let {
                    map.mapObjects.remove(it)
                }
                val location = locationResult.locations[0]
                markerCurLocation = map.mapObjects.addPlacemark(Point(location.latitude, location.longitude),
                ImageProvider.fromResource(requireContext(), R.drawable.yandex_logo_24))
                map.move(
                    CameraPosition(Point(location.latitude, location.longitude), 14.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1.0f),
                    null
                )
            }
        }
    }

    private var runningLocationUpdate = false
    private fun startLocationUpdate() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        runningLocationUpdate = true
    }

    private fun stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        runningLocationUpdate = false
    }

    //***************GPS****************** end

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

            markerGpsPoinnt = map.mapObjects.addPlacemark(
                it,
                ImageProvider.fromResource(requireContext(), R.drawable.mark_24)
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

        binding.btnCurLocation.setOnClickListener {
            if(runningLocationUpdate){
                binding.btnCurLocation.setImageResource(R.drawable.baseline_location_off_24)
                stopLocationUpdate()
            } else {
                binding.btnCurLocation.setImageResource(R.drawable.baseline_location_on_24)
                startLocationUpdate()
            }
        }

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
        if(runningLocationUpdate)
            stopLocationUpdate()
        map.removeInputListener(this)
        super.onStop()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        Log.d("MapFragment", "onStop end")
    }

    override fun onMapTap(m: Map, p: Point) {
        markerGpsPoinnt?.let {
            map.mapObjects.remove(it)
        }
        markerGpsPoinnt = map.mapObjects.addPlacemark(
            p,
            ImageProvider.fromResource(requireContext(), R.drawable.mark_24)
        )
        model.editedPoint.gpsPoint = Point(p.latitude, p.longitude)
        Log.d("MapFragment", "onMapTap map=$m,point=$p")
    }

    override fun onMapLongTap(m: Map, p: Point) {
        Log.d("MapFragment", "onMapLongTap map=$m,point=$p")
    }
}