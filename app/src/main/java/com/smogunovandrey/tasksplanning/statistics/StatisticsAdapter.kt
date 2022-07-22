package com.smogunovandrey.tasksplanning.statistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smogunovandrey.tasksplanning.databinding.ItemStatisticsBinding

class StatisticsAdapter :
    ListAdapter<StatisticPointItem, StatisticsAdapter.StatisticsViewHolder>(diffUtilsStatistics) {
    class StatisticsViewHolder(val binding: ItemStatisticsBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val binding =
            ItemStatisticsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatisticsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val statisticsItem = getItem(position)
        holder.binding.statisticsItem = statisticsItem
        if(statisticsItem.numPoint == 1)
            holder.binding.cnstrntContent.visibility = View.GONE
        else
            holder.binding.cnstrntContent.visibility = View.VISIBLE
    }
}

private object diffUtilsStatistics : DiffUtil.ItemCallback<StatisticPointItem>() {
    override fun areItemsTheSame(
        oldItem: StatisticPointItem,
        newItem: StatisticPointItem
    ): Boolean {
        return oldItem.idPoint == newItem.idPoint
    }

    override fun areContentsTheSame(
        oldItem: StatisticPointItem,
        newItem: StatisticPointItem
    ): Boolean {
        return oldItem == newItem
    }

}