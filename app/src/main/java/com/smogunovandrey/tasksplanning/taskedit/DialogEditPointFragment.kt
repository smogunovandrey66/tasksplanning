package com.smogunovandrey.tasksplanning.taskedit

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.databinding.FragmentDialogEditPointBinding
import com.smogunovandrey.tasksplanning.db.TriggerType
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel
import kotlinx.coroutines.launch

class DialogEditPointFragment: DialogFragment() {
    private val model: TaskTemplateViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(activity)
            .setPositiveButton(if(model.flagEditPoint) R.string.edit else R.string.add){
                dialog, which ->
                    if(model.flagEditPoint){
                        lifecycleScope.launch {
                            model.updatePoint(model.editedPoint)
                        }
                    } else {
                        lifecycleScope.launch {
                            model.addPoint(model.editedPoint)
                        }
                    }
            }
            .setNegativeButton(R.string.cancel){
                dialog, which ->
            }
            .setView(FragmentDialogEditPointBinding.inflate(layoutInflater)
                .apply {
                    val listTrigger = TriggerType.values().map {
                        it.name
                    }
                    val point = model.editedPoint

                    spnTriggerType.adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, listTrigger)

                    if(model.flagEditPoint){
                        edtName.setText(point.name)
                        spnTriggerType.setSelection(listTrigger.indexOf(point.triggerType.name))
                        edtNumber.setText(point.num.toString())
                    }
                }
                .root)
            .create()

    companion object{
        const val TAG = "tag DialogEditPointFragment"
    }
}