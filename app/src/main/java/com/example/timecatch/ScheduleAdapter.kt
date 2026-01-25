package com.example.timecatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timecatch.data.ScheduleEntity

// 1. 생성자에서 onDeleteClick 함수를 받도록 수정합니다.
class ScheduleAdapter(private val onDeleteClick: (ScheduleEntity) -> Unit) :
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    private var items = listOf<ScheduleEntity>()

    fun setItems(newList: List<ScheduleEntity>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvTime.text = "${item.startTime} ~ ${item.endTime}"
        holder.tvTitle.text = item.title

        // 2. 뷰홀더에 저장된 버튼에 클릭 리스너를 연결합니다.
        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        // 3. 삭제 버튼을 홀더에 미리 찾아둡니다.
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }
}