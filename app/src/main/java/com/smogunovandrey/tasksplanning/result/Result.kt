package com.smogunovandrey.tasksplanning.result

import com.smogunovandrey.tasksplanning.db.TaskWithPointDB
import java.lang.Exception

sealed class Result {
    class Succes(val data: List<TaskWithPointDB>)
    class Error(val exception: Exception)
}