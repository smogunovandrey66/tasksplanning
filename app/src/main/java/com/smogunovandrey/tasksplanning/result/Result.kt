package com.smogunovandrey.tasksplanning.result

import com.smogunovandrey.tasksplanning.db.TaskWithPoint
import java.lang.Exception

sealed class Result {
    class Succes(val data: List<TaskWithPoint>)
    class Error(val exception: Exception)
}