package com.smogunovandrey.tasksplanning.taskedit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smogunovandrey.tasksplanning.databinding.ItemPointsTemplateBinding
import com.smogunovandrey.tasksplanning.taskstemplate.Point

class AdapterEditPoints(private var points: MutableList<Point> = mutableListOf(), private var onClickPoint: OnClickPoint? = null): RecyclerView.Adapter<AdapterEditPoints.ViewHolderPointItem>() {

    interface OnClickPoint{
        fun onClick(point: Point)
    }

    class ViewHolderPointItem(val binding: ItemPointsTemplateBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPointItem {
        //Need parent for android:layout_width="match_parent"
        val binding: ItemPointsTemplateBinding = ItemPointsTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setOnClickListener {
            val point = binding.point
            if(point != null)
                onClickPoint?.onClick(point)
        }
        return ViewHolderPointItem(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderPointItem, position: Int) {
        holder.binding.point = points[position]
    }

    override fun getItemCount() = points.count()
}