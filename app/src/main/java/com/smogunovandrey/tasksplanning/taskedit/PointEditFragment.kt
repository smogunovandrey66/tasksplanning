package com.smogunovandrey.tasksplanning.taskedit

import android.location.Location
import android.os.Bundle
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
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.databinding.FragmentPointEditBinding
import com.smogunovandrey.tasksplanning.db.TriggerType
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel
import com.smogunovandrey.tasksplanning.taskstemplate.Point

class PointEditFragment: Fragment() {
    private val model: TaskTemplateViewModel by activityViewModels()
    private val binding by lazy{
        FragmentPointEditBinding.inflate(layoutInflater)
    }

    private val point by lazy {
        model.editedPoint
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        model.posPointInserted = null
        model.posPointUpdate = null

        val listTrigger = TriggerType.values().map {
            it.name
        }
        binding.spnTriggerType.adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, listTrigger)

        binding.edtName.setText(point.name)
        binding.spnTriggerType.setSelection(listTrigger.indexOf(point.triggerType.name))
        binding.txtNumber.text = point.num.toString()
        updateGpsPoint()
        binding.txtLocationInfo.setOnClickListener {
            findNavController().navigate(R.id.mapFragment)
        }


        binding.edtName.addTextChangedListener {
            point.name = it.toString()
        }
        binding.spnTriggerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                point.triggerType = TriggerType.values()[position]
                Log.d("PointEditFragment", "triggerType=${point.triggerType}")
                if(point.triggerType == TriggerType.GPS_IN){
                    val fUsedLocationClient =  LocationServices.getFusedLocationProviderClient(requireActivity())
                    fUsedLocationClient.lastLocation.addOnSuccessListener {location: Location? ->
                        Log.d("PointEditFragment", "location=${location}")
                        location?.let {
                            val pointGps: com.yandex.mapkit.geometry.Point = com.yandex.mapkit.geometry.Point(it.latitude, it.longitude)
                            point.gpsPoint = pointGps
                            Log.d("PointEditFragment", "pointGps=$pointGps")
                        }
                        updateGpsPoint()
                    }.addOnFailureListener {exception ->
                        Log.d("PointEditFragment", "exception=$exception")
                        updateGpsPoint()
                    }
                } else {
                    point.gpsPoint = null
                    updateGpsPoint()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        if(model.flagEditPoint)
            binding.btnAddPoint.setText(com.smogunovandrey.tasksplanning.R.string.edit)

        binding.btnAddPoint.setOnClickListener {
            if(canSave()){
                if(model.flagEditPoint){
                    model.selectedPoint.copy(point)
                    model.posPointUpdate = point.num.toInt() - 1
                } else {
                    model.editedTaskWithPoints.points.add(Point().copy(point))
                    model.posPointInserted = point.num.toInt() - 1
                }
                findNavController().popBackStack()
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun canSave(): Boolean{
        if(model.editedPoint.name == ""){
            Snackbar.make(binding.btnAddPoint, com.smogunovandrey.tasksplanning.R.string.empty_name_point, Snackbar.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun updateGpsPoint(){
        Log.d("PointEditFragment", "updateGpsPoint point.gpsPoint=${point.gpsPoint}")
        if(point.gpsPoint == null){
            binding.txtLocationInfo.visibility = View.GONE
        } else {
            binding.txtLocationInfo.visibility = View.VISIBLE
            binding.txtLocationInfo.text = "${point.gpsPoint!!.latitude},${point.gpsPoint!!.longitude}"
        }
    }
}