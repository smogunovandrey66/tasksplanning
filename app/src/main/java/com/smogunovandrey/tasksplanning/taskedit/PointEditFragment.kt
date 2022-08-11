package com.smogunovandrey.tasksplanning.taskedit

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.smogunovandrey.tasksplanning.ACCESS_FINE_LOCATION
import com.smogunovandrey.tasksplanning.PERMISSION_GRANTED
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.databinding.FragmentPointEditBinding
import com.smogunovandrey.tasksplanning.db.TriggerType
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel
import com.smogunovandrey.tasksplanning.taskstemplate.Point
import com.smogunovandrey.tasksplanning.taskstemplate.Task

class PointEditFragment : Fragment() {
    private val model: TaskTemplateViewModel by activityViewModels()
    private val binding by lazy {
        FragmentPointEditBinding.inflate(layoutInflater)
    }

    private val editedPoint by lazy {
        model.editedPoint
    }

    //***************GPS****************** begin

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private val locationRequest = com.google.android.gms.location.LocationRequest()
        .setInterval(2000L)
        .setFastestInterval(1000L)
        .setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            Log.d("PointEditFragment", "locationResult=$locationResult")
            if (locationResult != null && locationResult.locations.size > 0) {
                editedPoint.gpsPoint = com.yandex.mapkit.geometry.Point(
                    locationResult.lastLocation.latitude,
                    locationResult.lastLocation.longitude
                )
                stopLocationUpdate()
            }
            updateGpsPoint()
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
        Log.d("PointEditFragment", "onCreateView,model=$model")
        model.posPointInserted = null
        model.posPointUpdate = null

        val listTrigger = TriggerType.values().map {
            it.name
        }
        binding.spnTriggerType.adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, listTrigger)

        binding.edtName.setText(editedPoint.name)
        binding.spnTriggerType.setSelection(listTrigger.indexOf(editedPoint.triggerType.name))
        binding.txtNumber.text = editedPoint.num.toString()
        updateGpsPoint()
        binding.txtLocationInfo.setOnClickListener {
            findNavController().navigate(R.id.mapFragment)
        }


        binding.edtName.addTextChangedListener {
            editedPoint.name = it.toString()
        }
        binding.spnTriggerType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val posSpinner = binding.spnTriggerType.selectedItemPosition
                    val posModel = listTrigger.indexOf(editedPoint.triggerType.name)
                    if (posSpinner == posModel)
                        return

                    Log.d(
                        "PointEditFragment",
                        "onItemSelectedListener posSpinner=$posSpinner,posModel=$posModel"
                    )

                    editedPoint.triggerType = TriggerType.values()[position]
                    Log.d(
                        "PointEditFragment",
                        "onItemSelectedListener triggerType=${editedPoint.triggerType}"
                    )
                    if (editedPoint.triggerType == TriggerType.GPS_IN) {
                        if (requireActivity().checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
                            startLocationUpdate()
                        }
//                    val fUsedLocationClient =  LocationServices.getFusedLocationProviderClient(requireActivity())
//                    fUsedLocationClient.lastLocation.addOnSuccessListener {location: Location? ->
//                        Log.d("PointEditFragment", "location=${location}")
//                        location?.let {
//                            val pointGps: com.yandex.mapkit.geometry.Point = com.yandex.mapkit.geometry.Point(it.latitude, it.longitude)
//                            editedPoint.gpsPoint = pointGps
//                            Log.d("PointEditFragment", "pointGps=$pointGps")
//                        }
//                        updateGpsPoint()
//                    }.addOnFailureListener {exception ->
//                        Log.d("PointEditFragment", "exception=$exception")
//                        updateGpsPoint()
//                    }
                    } else {
                        editedPoint.gpsPoint = null
                        if(runningLocationUpdate)
                            stopLocationUpdate()
                    }
                    updateGpsPoint()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

        if (model.flagEditPoint)
            binding.btnAddPoint.setText(com.smogunovandrey.tasksplanning.R.string.edit)

        binding.btnAddPoint.setOnClickListener {
            if (canSave()) {
                if (model.flagEditPoint) {
                    model.selectedPoint.copy(editedPoint)
                    model.posPointUpdate = editedPoint.num.toInt() - 1
                } else {
                    model.editedTaskWithPoints.points.add(Point().copy(editedPoint))
                    model.posPointInserted = editedPoint.num.toInt() - 1
                }
                findNavController().popBackStack()
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun canSave(): Boolean {
        if (model.editedPoint.name == "") {
            Snackbar.make(
                binding.btnAddPoint,
                com.smogunovandrey.tasksplanning.R.string.empty_name_point,
                Snackbar.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    private fun updateGpsPoint() {
        Log.d("PointEditFragment", "updateGpsPoint point.gpsPoint=${editedPoint.gpsPoint}")
        if (editedPoint.gpsPoint == null) {
            if (editedPoint.triggerType == TriggerType.GPS_IN) {
                binding.txtLocationInfo.visibility = View.VISIBLE
                binding.txtLocationInfo.text = "Can't find gps location"
            } else
                binding.txtLocationInfo.visibility = View.GONE
        } else {
            binding.txtLocationInfo.visibility = View.VISIBLE
            binding.txtLocationInfo.text =
                "${editedPoint.gpsPoint!!.latitude},${editedPoint.gpsPoint!!.longitude}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("PointEditFragment", "onDestroy")
    }
}