package com.example.timecatch

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.timecatch.databinding.ItemGroupBinding

// 생성자에서 (Group)->Unit 제거함! (복잡한 거 뺌)
class GroupAdapter : ListAdapter<Group, GroupAdapter.GroupViewHolder>(GroupDiffCallback()) {

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

            // 확정 여부 UI
            if (group.confirmedTime != null) {
                binding.tvGroupDate.text = "✅ 확정: ${group.confirmedTime}"
                binding.tvGroupDate.setTextColor(android.graphics.Color.parseColor("#2D2FA8"))
                binding.tvGroupDate.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                binding.tvGroupDate.text = group.targetDate
                binding.tvGroupDate.setTextColor(android.graphics.Color.parseColor("#888888"))
                binding.tvGroupDate.setTypeface(null, android.graphics.Typeface.NORMAL)
            }

            // ★★★ 여기가 수정된 핵심! (Adapter에서 바로 이동) ★★★
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, GroupDetailActivity::class.java)

                // 받는 쪽(DetailActivity)이랑 암호(Key)를 똑같이 맞춰줌
                intent.putExtra("GROUP_ID", group.id)
                intent.putExtra("GROUP_NAME", group.groupName)

                context.startActivity(intent)
            }
        }
    }

    class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Group, newItem: Group) = oldItem == newItem
    }
}