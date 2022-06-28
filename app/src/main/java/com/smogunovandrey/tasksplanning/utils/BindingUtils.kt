package com.smogunovandrey.tasksplanning.utils

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.smogunovandrey.tasksplanning.db.TriggerType


@BindingAdapter("android:trigger_type")
fun bindTriggerType(view: TextView, trigerType: TriggerType){
    view.text = "($trigerType)"
}