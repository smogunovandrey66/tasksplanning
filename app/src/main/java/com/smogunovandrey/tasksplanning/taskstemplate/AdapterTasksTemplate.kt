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


class AdapterTasksTemplate :
    ListAdapter<Task, AdapterTasksTemplate.TaskItemHolder>(DiffUtilsTasks) {

    class TaskItemHolder(val binding: ItemTasksTemplateBinding) :
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
                val appContext = itemView.context.applicationContext
//                val intent = Intent(appContext, RunService::class.java).apply { action = "intent" }
//                val pendingIntent = PendingIntent.getForegroundService(appContext, 1, intent, PendingIntent.FLAG_MUTABLE)
//                pendingIntent.send()
//                Log.d("RunService", "pendingIntent=$pendingIntent")
//
//                val intent2 = Intent(appContext, RunService::class.java).apply { action = "intent2" }
//                val pendingIntent2 = PendingIntent.getForegroundService(appContext, 1, intent2, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE)
//                Log.d("RunService", "pendingIntent2=$pendingIntent2")
                RunService.runTask(
                    appContext, RunTaskNotification(
                        1, 1, "Lift", 4, 1, 1, false
                    )
                )
//                val intent = Intent(itemView.context, RunService::class.java)
//                ContextCompat.startForegroundService(itemView.context.applicationContext, intent)
//                val intent = Intent(itemView.context.applicationContext, RunBroadcastReceiver::class.java).apply {
//                    putExtra("idTask", task.id)
//                }
//                itemView.context.applicationContext.sendBroadcast(intent)

                val task = binding.taskItem
                return@setOnClickListener
                task?.let {
                    val action =
                        TasksTemplateFragmentDirections.actionTasksTemplateFragmentToRunTaskViewFragment(
                            0,
                            it.id
                        )
                    itemView.findNavController().navigate(action)
                }
            }

            binding.btnStatistics.setOnClickListener {
                val action =
                    TasksTemplateFragmentDirections.actionTasksTemplateFragmentToStatisticsFragment()
                itemView.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemHolder {
        val binding =
            ItemTasksTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskItemHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskItemHolder, position: Int) {
        holder.task = getItem(position)
        holder.binding.taskItem = holder.task
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