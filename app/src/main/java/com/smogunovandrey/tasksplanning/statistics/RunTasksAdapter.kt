package com.smogunovandrey.tasksplanning.statistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smogunovandrey.tasksplanning.databinding.ItemRunTaskBinding
import com.smogunovandrey.tasksplanning.taskstemplate.DiffUtilsTasks
import com.smogunovandrey.tasksplanning.taskstemplate.RunTask

interface OnRunTaskClick{
    fun onRunTaskClick(runTask: RunTask)
}
class RunTasksAdapter(var onRunTaskClick: OnRunTaskClick? = null): ListAdapter<RunTask, RunTasksAdapter.RunTasksHolder>(diffUtilsRunTasks) {

    class RunTasksHolder(val binding: ItemRunTaskBinding, val onRunTaskClick: OnRunTaskClick? = null) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val runTask = binding.runTaskItem
                runTask?.let{
                    onRunTaskClick?.onRunTaskClick(it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunTasksHolder {
        val binding = ItemRunTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  RunTasksHolder(binding, onRunTaskClick)
    }

    override fun onBindViewHolder(holder: RunTasksHolder, position: Int) {
        holder.binding.runTaskItem = getItem(position)
    }
}

private object diffUtilsRunTasks: DiffUtil.ItemCallback<RunTask>() {
    override fun areItemsTheSame(oldItem: RunTask, newItem: RunTask): Boolean {
        return oldItem.idRunTask == newItem.idRunTask
    }

    override fun areContentsTheSame(oldItem: RunTask, newItem: RunTask): Boolean {
        return oldItem == newItem
    }

}