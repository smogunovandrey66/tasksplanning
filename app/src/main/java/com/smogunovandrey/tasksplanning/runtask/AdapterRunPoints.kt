package com.smogunovandrey.tasksplanning.runtask

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smogunovandrey.tasksplanning.databinding.ItemRunPointBinding
import com.smogunovandrey.tasksplanning.taskstemplate.RunPoint

class AdapterRunPoints: ListAdapter<RunPoint, AdapterRunPoints.RunPointHolder>(diffUtilCallback) {
    init {
        Log.d("AdapterRunPoints", "init")
    }
    class RunPointHolder(val binding: ItemRunPointBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunPointHolder {
        Log.d("AdapterRunPoints", "onCreateViewHolder")
        val binding = ItemRunPointBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RunPointHolder(binding)
    }

    override fun onBindViewHolder(holder: RunPointHolder, position: Int) {
        holder.binding.item = getItem(position)
    }
}

object diffUtilCallback: DiffUtil.ItemCallback<RunPoint>(){
    override fun areItemsTheSame(oldItem: RunPoint, newItem: RunPoint): Boolean {
        Log.d("AdapterRunPoints", "areItemsTheSame")
        return oldItem.num == newItem.num
    }

    override fun areContentsTheSame(oldItem: RunPoint, newItem: RunPoint): Boolean {
        Log.d("AdapterRunPoints", "areContentsTheSame")
        return oldItem == newItem
    }

}