package com.example.timecatch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.timecatch.databinding.ItemGroupBinding

class GroupAdapter(private val onItemClick: (Group) -> Unit) :
    ListAdapter<Group, GroupAdapter.GroupViewHolder>(GroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GroupViewHolder(private val binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: Group) {
            binding.tvGroupName.text = group.groupName

            // ▼▼▼ [수정] 확정 여부에 따라 다르게 표시하기 ▼▼▼
            if (group.confirmedTime != null) {
                // 1. 확정된 시간이 있을 때 (파란색 강조)
                binding.tvGroupDate.text = "✅ 확정: ${group.confirmedTime}"
                binding.tvGroupDate.setTextColor(android.graphics.Color.parseColor("#2D2FA8"))
                binding.tvGroupDate.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                // 2. 아직 미정일 때 (원래 날짜 표시, 회색)
                binding.tvGroupDate.text = group.targetDate
                binding.tvGroupDate.setTextColor(android.graphics.Color.parseColor("#888888"))
                binding.tvGroupDate.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
            binding.root.setOnClickListener {
                onItemClick(group)
            }
        }
    }

    class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
        // ★ 여기가 수정 포인트! groupId 대신 id를 사용합니다.
        override fun areItemsTheSame(oldItem: Group, newItem: Group) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Group, newItem: Group) = oldItem == newItem
    }
}