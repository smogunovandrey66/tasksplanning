package com.smogunovandrey.tasksplanning.utils

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.smogunovandrey.tasksplanning.db.TriggerType
import java.util.Date


@SuppressLint("SetTextI18n")
@BindingAdapter("android:trigger_type")
fun bindTriggerType(view: TextView, trigerType: TriggerType){
    view.text = "($trigerType)"
}

@BindingAdapter("android:duration")
fun bindDurationRunTask(view: TextView, duration: Long?){
    duration?.let {
        view.text = it.toString()
        return
    }
    view.text = ""
}

@BindingAdapter("android:date")
fun bindDateRunTask(view: TextView, date: Date?){
    date?.let {
        view.text = it.toString()
        return
    }
    view.text = ""
}