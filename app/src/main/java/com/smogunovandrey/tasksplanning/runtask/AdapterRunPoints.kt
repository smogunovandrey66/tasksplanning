package com.smogunovandrey.tasksplanning.runtask

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smogunovandrey.tasksplanning.databinding.ItemRunPointBinding
import com.smogunovandrey.tasksplanning.taskstemplate.RunPoint

class AdapterRunPoints: ListAdapter<RunPoint, AdapterRunPoints.RunPointHolder>(diffUtilCallback) {
    class RunPointHolder(val binding: ItemRunPointBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunPointHolder {
        val binding = ItemRunPointBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RunPointHolder(binding)
    }

    override fun onBindViewHolder(holder: RunPointHolder, position: Int) {
        holder.binding.item = getItem(position)
        if(position == 0){
            holder.binding.txtDuration.visibility = View.GONE
            holder.binding.txtDurationLabel.visibility = View.GONE
        } else {
            holder.binding.txtDuration.visibility = View.VISIBLE
            holder.binding.txtDurationLabel.visibility = View.VISIBLE
        }
    }
}

object diffUtilCallback: DiffUtil.ItemCallback<RunPoint>(){
    override fun areItemsTheSame(oldItem: RunPoint, newItem: RunPoint): Boolean {
        return oldItem.num == newItem.num
    }

    override fun areContentsTheSame(oldItem: RunPoint, newItem: RunPoint): Boolean {
        return oldItem == newItem
    }

}