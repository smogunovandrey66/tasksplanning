package com.smogunovandrey.tasksplanning.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Task item for persist database
 */

@Entity
data class TaskDb(
     @PrimaryKey(autoGenerate = true) val id: Long,
     val name: String
)