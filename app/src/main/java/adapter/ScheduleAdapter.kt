package com.example.timecatch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timecatch.R
import com.example.timecatch.ScheduleEntity

class ScheduleAdapter(
    private var items: List<ScheduleEntity>,
    private val onDeleteClick: (ScheduleEntity) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val btnDelete: View = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvTime.text = "${item.startTime} ~ ${item.endTime}"
        holder.tvTitle.text = item.title
        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<ScheduleEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}
