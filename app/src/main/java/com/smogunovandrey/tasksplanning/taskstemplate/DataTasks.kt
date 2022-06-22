package com.smogunovandrey.tasksplanning.taskstemplate

import com.smogunovandrey.tasksplanning.db.TriggerType

data class Point(
    val id: Long,
    val name: String,
    val num: Long,
    val triggerType: TriggerType
)


data class Task(
    val id: Long,
    val name: String
)