package com.smogunovandrey.tasksplanning.taskstemplate

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.databinding.ItemTasksTemplateBinding
import com.smogunovandrey.tasksplanning.db.TriggerType
import com.smogunovandrey.tasksplanning.taskedit.TaskEditFragment

data class PointItem(
    val id: Long,
    val name: String,
    val num: Long,
    val triggerType: TriggerType
)

data class TaskItem(
    val id: Long,
    val name: String,
    val points: MutableList<PointItem> = mutableListOf<PointItem>()
)



class AdapterTasksTemplate: ListAdapter<TaskItem, AdapterTasksTemplate.TaskItemHolder>(DiffUtilsTasks) {

    class TaskItemHolder(val binding: ItemTasksTemplateBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.name.setOnClickListener

                itemView.findNavController().navigate(R.id.taskEditFragment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemHolder {
        val binding = ItemTasksTemplateBinding.inflate(LayoutInflater.from(parent.context))
        return TaskItemHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskItemHolder, position: Int) {
        holder.binding.taskItem = getItem(position)

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