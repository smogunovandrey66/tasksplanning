package com.smogunovandrey.tasksplanning.taskstemplate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.databinding.ItemTasksTemplateBinding
import com.smogunovandrey.tasksplanning.runtask.RunBroadcastReceiver
import com.smogunovandrey.tasksplanning.runtask.RunService
import com.smogunovandrey.tasksplanning.runtask.RunTaskNotification

interface OnRunTaskItemClick {
    fun onRunTaskItemClick(task: Task)
}

class AdapterTasksTemplate :
    ListAdapter<Task, AdapterTasksTemplate.TaskItemHolder>(DiffUtilsTasks) {

    var onRunTaskItemClick: OnRunTaskItemClick? = null
    var activeRunTaskWithPoints: RunTaskWithPoints? = null

    class TaskItemHolder(
        val binding: ItemTasksTemplateBinding,
        val onRunTaskItemClick: OnRunTaskItemClick? = null,
        val activeRunTaskWithPoints: RunTaskWithPoints? = null
    ) :
        RecyclerView.ViewHolder(binding.root) {
        var task: Task = Task()

        init {
            binding.name.setOnClickListener {
                binding.taskItem?.let {
                    val action =
                        TasksTemplateFragmentDirections.actionTasksTemplateFragmentToTaskViewFragment(
                            it.id
                        )
                    itemView.findNavController().navigate(action)
                }

                //Other way navigate to taskFragment
//                itemView.findNavController().navigate(R.id.taskEditFragment)
            }

            binding.btnStart.setOnClickListener {
//                val appContext = itemView.context.applicationContext
//                RunService.runTask(
//                    appContext, RunTaskNotification(
//                        1, 1, "Lift", 6, 1, 1, false
//                    )
//                )

                val task = binding.taskItem
                task?.let {
                    val idTask = task.id
                    onRunTaskItemClick?.let {
                        it.onRunTaskItemClick(task)
                    }
                }
            }

            binding.btnStatistics.setOnClickListener {
                val action =
                    TasksTemplateFragmentDirections.actionTasksTemplateFragmentToStatisticsFragment(task.id)
                itemView.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemHolder {
        val binding =
            ItemTasksTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskItemHolder(binding, onRunTaskItemClick, activeRunTaskWithPoints)
    }

    override fun onBindViewHolder(holder: TaskItemHolder, position: Int) {
        holder.task = getItem(position)
        holder.binding.taskItem = holder.task
        holder.binding.activeRunTask = holder.activeRunTaskWithPoints

        val button = holder.binding.btnStart
        val task = holder.task
        val activeRunTask = holder.activeRunTaskWithPoints

//        Log.d("TasksTemplateFragment", "onBindViewHolder task=$task,activeRunTask=$activeRunTask")

        //Login for text and enable of button
        if(activeRunTask == null){
            button.text = button.context.getString(R.string.run)
            button.isEnabled = true
        } else {
            if(activeRunTask.runTask.idTask == task.id){
                button.text = button.context.getString(R.string.view)
                button.isEnabled = true
            } else {
                button.text = button.context.getString(R.string.run)
                button.isEnabled = false
            }
        }
    }
}

object DiffUtilsTasks : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }

}