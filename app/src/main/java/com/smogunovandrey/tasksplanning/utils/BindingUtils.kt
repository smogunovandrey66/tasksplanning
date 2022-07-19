package com.smogunovandrey.tasksplanning.utils

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.db.TriggerType
import com.smogunovandrey.tasksplanning.taskstemplate.RunTaskWithPoints
import com.smogunovandrey.tasksplanning.taskstemplate.Task
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

@BindingAdapter("android:status_button_next")
fun bindStatusButtonNext(view: Button, runTaskWithPoints: RunTaskWithPoints?){
    val runTask = runTaskWithPoints?.runTask
    if(runTask != null){
        view.visibility = View.VISIBLE
        if(runTask.dateCreate == null){
            view.text = view.context.getString(R.string.start)
            return
        }

        var existNull = false
        for(item in runTaskWithPoints.points){
            if(item.dateMark == null){
                existNull = true
                break
            }
        }

        if(existNull)
            view.text = view.context.getString(R.string.next)
        else
            view.visibility = View.GONE

    } else {
        view.visibility = View.GONE
    }
}