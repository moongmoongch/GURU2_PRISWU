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
            // 변수 이름이 바뀌었어도 걱정 마세요.
            // Group.kt에 정의된 변수명(groupName, targetDate)을 그대로 씁니다.
            binding.tvGroupName.text = group.groupName
            binding.tvGroupDate.text = group.targetDate ?: "날짜 미정"

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