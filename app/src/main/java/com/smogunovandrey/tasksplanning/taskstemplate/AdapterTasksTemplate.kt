package com.smogunovandrey.tasksplanning.taskstemplate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smogunovandrey.tasksplanning.databinding.ItemTasksTemplateBinding
import com.smogunovandrey.tasksplanning.db.TriggerType


class AdapterTasksTemplate: ListAdapter<Task, AdapterTasksTemplate.TaskItemHolder>(DiffUtilsTasks) {

    class TaskItemHolder(val binding: ItemTasksTemplateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.name.setOnClickListener {
                binding.taskItem?.let {
                    val action = TasksTemplateFragmentDirections.actionTasksTemplateFragmentToTaskViewFragment(it.id)
                    itemView.findNavController().navigate(action)
                }

                binding.btnStart.setOnClickListener {
                    val action = TasksTemplateFragmentDirections.actionTasksTemplateFragmentToRunTaskViewFragment()
                    itemView.findNavController().navigate(action)
                }

                binding.btnStatistics.setOnClickListener {
                    val action = TasksTemplateFragmentDirections.actionTasksTemplateFragmentToStatisticsFragment()
                    itemView.findNavController().navigate(action)
                }


                //Other way navigate to taskFragment
//                itemView.findNavController().navigate(R.id.taskEditFragment)
            }
        }
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemHolder {
            val binding = ItemTasksTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return  TaskItemHolder(binding)
        }

        override fun onBindViewHolder(holder: TaskItemHolder, position: Int) {
            holder.binding.taskItem = getItem(position)
        }
}

object DiffUtilsTasks: DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }

}