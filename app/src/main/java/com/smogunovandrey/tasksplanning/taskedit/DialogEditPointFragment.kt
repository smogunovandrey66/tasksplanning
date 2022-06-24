package com.smogunovandrey.tasksplanning.taskedit

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.databinding.FragmentDialogEditPointBinding
import com.smogunovandrey.tasksplanning.db.TriggerType
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel

class DialogEditPointFragment: DialogFragment() {
    private val model: TaskTemplateViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(activity)
            .setPositiveButton(R.string.add){
                dialog, which ->
            }
            .setNegativeButton(R.string.cancel){
                dialog, which ->
            }
            .setView(FragmentDialogEditPointBinding.inflate(layoutInflater)
                .apply {
                    val listTrigger = TriggerType.values().map {
                        it.name
                    }
                    spnTriggerType.adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, listTrigger)

                }
                .root)
            .create()

    companion object{
        const val TAG = "tag DialogEditPointFragment"
    }
}