package com.smogunovandrey.tasksplanning.taskstemplate

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

data class TaskItem(
    val id: Long,
    val name: String
)

class AdapterTasksTemplate: ListAdapter<TaskItem, AdapterTasksTemplate.TaskItemHolder>(DiffUtilsTasks) {

    class TaskItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: TaskItemHolder, position: Int) {
        TODO("Not yet implemented")
    }


}

object DiffUtilsTasks: DiffUtil.ItemCallback<TaskItem>() {
    override fun areItemsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
        return oldItem == newItem
    }

}