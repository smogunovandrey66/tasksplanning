package com.smogunovandrey.tasksplanning.taskedit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smogunovandrey.tasksplanning.databinding.ItemPointsTemplateBinding
import com.smogunovandrey.tasksplanning.taskstemplate.Point


class AdapterEditPoints(var points: MutableList<Point> = mutableListOf()): RecyclerView.Adapter<AdapterEditPoints.ViewHolderPointItem>() {

    class ViewHolderPointItem(val binding: ItemPointsTemplateBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPointItem {
        //Need parent for android:layout_width="match_parent"
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