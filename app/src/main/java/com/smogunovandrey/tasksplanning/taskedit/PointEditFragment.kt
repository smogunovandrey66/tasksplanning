package com.smogunovandrey.tasksplanning.taskedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.cardview.R
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.smogunovandrey.tasksplanning.databinding.FragmentDialogEditPointBinding
import com.smogunovandrey.tasksplanning.db.TriggerType
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel
import com.smogunovandrey.tasksplanning.utils.swap
import kotlinx.coroutines.launch

class PointEditFragment: Fragment() {
    private val model: TaskTemplateViewModel by activityViewModels()
    private val binding by lazy{
        FragmentDialogEditPointBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val listTrigger = TriggerType.values().map {
            it.name
        }
        val point = model.editedPoint

        binding.spnTriggerType.adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, listTrigger)

        binding.edtName.setText(point.name)
        binding.spnTriggerType.setSelection(listTrigger.indexOf(point.triggerType.name))
        binding.edtNumber.setText(point.num.toString())


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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        binding.edtNumber.addTextChangedListener {
            val str = it.toString()
            if(str.isDigitsOnly()){
                point.num = str.toLong()
            }
        }

        if(model.flagEditPoint)
            binding.btnAddPoint.setText(com.smogunovandrey.tasksplanning.R.string.edit)

        binding.btnAddPoint.setOnClickListener {
            if(canSave()){
                if(model.flagEditPoint){
                    var fromPos = 0
                    var toPos = 0
                    var num = 0L


                    for(item in model.editedTaskWithPoints.points){
                        num++
                        if(item.id == point.id){
                            fromPos = num.toInt() - 1
                        }
                       if(item.num == point.num){
                           toPos = num.toInt() - 1
                       }
                    }
                    if(fromPos != toPos){
                        model.editedTaskWithPoints.points.swap(fromPos, toPos)
                        model.editedTaskWithPoints.points[fromPos].num = fromPos + 1L
                        model.editedTaskWithPoints.points[toPos].num = toPos + 1L
                    }
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    fun canSave(): Boolean{
        if(model.editedPoint.name == ""){
            Snackbar.make(binding.btnAddPoint, "Name point empty", Snackbar.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}