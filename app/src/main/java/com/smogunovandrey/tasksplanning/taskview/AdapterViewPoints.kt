package com.smogunovandrey.tasksplanning.taskview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smogunovandrey.tasksplanning.databinding.ItemPointsTemplateBinding
import com.smogunovandrey.tasksplanning.taskstemplate.Point


class AdapterViewPoints(var points: List<Point>): RecyclerView.Adapter<AdapterViewPoints.ViewHolderPointItem>() {

    class ViewHolderPointItem(val binding: ItemPointsTemplateBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPointItem {
        val binding: ItemPointsTemplateBinding = ItemPointsTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderPointItem(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderPointItem, position: Int) {
        holder.binding.point = points[position]
    }

    override fun getItemCount(): Int {
        return points.count()
    }
}