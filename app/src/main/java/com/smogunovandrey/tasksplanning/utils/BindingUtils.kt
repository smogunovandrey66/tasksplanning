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
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds


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
    view.text = "date null"
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

@BindingAdapter("android:duration_time")
fun bindDurationTime(txt: TextView, duration: Long?){
    if(duration != null){
        val strDuration = duration.milliseconds.toComponents { days, hours, minutes, seconds, _ ->
            var res = "%02d:%02d:%02d".format(hours, minutes, seconds)
            if(days > 0){
                val daysStr = if(days == 1L) "%d day ".format(days) else "%d days "
                res = daysStr + res
            }

            res
        }

        txt.text = strDuration
    } else
        txt.text = "null"
}