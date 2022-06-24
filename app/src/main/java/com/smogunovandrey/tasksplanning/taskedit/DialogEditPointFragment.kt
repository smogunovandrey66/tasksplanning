package com.smogunovandrey.tasksplanning.taskedit

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.appcompat.resources.R
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.smogunovandrey.tasksplanning.databinding.FragmentDialogEditPointBinding
import com.smogunovandrey.tasksplanning.db.TriggerType
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel

class DialogEditPointFragment: DialogFragment() {
    private val model: TaskTemplateViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        val binding = FragmentDialogEditPointBinding.inflate(layoutInflater)

        val listTrigger = TriggerType.values().map {
            it.name
        }

        binding.spnTriggerType.adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, listTrigger)

        builder.setView(binding.root)

        return builder.create()
    }

    companion object{
        const val TAG = "tag DialogEditPointFragment"
    }
}