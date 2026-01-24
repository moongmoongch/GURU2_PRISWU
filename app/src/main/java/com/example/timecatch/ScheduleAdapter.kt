package com.example.timecatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ScheduleAdapter(
    private val onDeleteClick: (ScheduleEntity) -> Unit
) : ListAdapter<ScheduleEntity, ScheduleAdapter.ScheduleViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ScheduleViewHolder(
        itemView: View,
        private val onDeleteClick: (ScheduleEntity) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val btnDelete: TextView = itemView.findViewById(R.id.btnDelete)

        fun bind(item: ScheduleEntity) {
            tvTime.text = "${item.startTime} ~ ${item.endTime}"
            tvTitle.text = item.title

            btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ScheduleEntity>() {
            override fun areItemsTheSame(oldItem: ScheduleEntity, newItem: ScheduleEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ScheduleEntity, newItem: ScheduleEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}
