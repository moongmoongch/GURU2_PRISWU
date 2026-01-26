package com.example.timecatch

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.timecatch.databinding.ItemGroupBinding

// [수정] 생성자에 'onGroupLongClick' 람다 함수(콜백)를 추가했습니다.
// (Int) -> Unit : 그룹 ID(Int)를 받아서 아무것도 반환하지 않는 함수라는 뜻입니다.
class GroupAdapter(
    private val onGroupLongClick: (Int) -> Unit
) : ListAdapter<Group, GroupAdapter.GroupViewHolder>(GroupDiffCallback()) {

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

            // 1. 확정 여부에 따른 UI 처리
            if (group.confirmedTime != null) {
                binding.tvGroupDate.text = "✅ 확정: ${group.confirmedTime}"
                binding.tvGroupDate.setTextColor(Color.parseColor("#2D2FA8"))
                binding.tvGroupDate.setTypeface(null, Typeface.BOLD)
            } else {
                binding.tvGroupDate.text = group.targetDate
                binding.tvGroupDate.setTextColor(Color.parseColor("#888888"))
                binding.tvGroupDate.setTypeface(null, Typeface.NORMAL)
            }

            // 2. [기존] 클릭 시 상세 화면 이동
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, GroupDetailActivity::class.java).apply {
                    putExtra("GROUP_ID", group.id)
                    putExtra("GROUP_NAME", group.groupName)
                }
                context.startActivity(intent)
            }

            // 3. [추가] 롱클릭 시 삭제 요청 (MainActivity로 신호 보냄)
            binding.root.setOnLongClickListener {
                onGroupLongClick(group.id) // 여기서 MainActivity의 다이얼로그 함수가 실행됨
                true // true를 반환해야 일반 클릭 이벤트와 겹치지 않음 (이벤트 소비)
            }
        }
    }

    class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Group, newItem: Group) = oldItem == newItem
    }
}