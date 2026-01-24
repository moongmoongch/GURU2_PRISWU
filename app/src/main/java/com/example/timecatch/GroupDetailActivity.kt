package com.example.timecatch

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.timecatch.databinding.ActivityGroupDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.timecatch.data.AppDatabase

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupDetailBinding
    private lateinit var db: AppDatabase
    private var groupId: Int = -1

    // 테스트용: true면 방장 권한(선택 버튼 보임), false면 팀원
    private val isLeader = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 팝업 느낌을 위한 투명 배경 설정
        window.setBackgroundDrawableResource(android.R.color.transparent)

        // 0. DB 초기화
        db = AppDatabase.getDatabase(this)

        // 1. Intent 데이터 받기
        val groupName = intent.getStringExtra("GROUP_NAME") ?: "이름 없음"
        groupId = intent.getIntExtra("GROUP_ID", -1)

        // UI 설정
        binding.tvGroupName.text = groupName
        binding.btnClose.setOnClickListener { finish() }

        // 2. 골든 타임 계산 및 표시 시작
        showGoldenTimeResults()
    }

    private fun showGoldenTimeResults() {
        // [STEP 1] 데이터를 가져온다. (함수로 분리됨!)
        // 지금은 가짜 데이터를 가져오지만, 나중엔 이 함수 안에서 DB를 뒤져올 것입니다.
        val (totalMembers, memberData) = fetchGroupSchedules(groupId)

        // [STEP 2] 알고리즘 가동
        val results = GoldenTimeFinder.analyze(totalMembers, memberData)

        // [STEP 3] 결과 UI 그리기
        binding.llResultContainer.removeAllViews() // 초기화

        if (results.isEmpty()) {
            val emptyView = TextView(this).apply {
                text = "겹치는 시간이 없음"
                textSize = 14f
                setPadding(0, 20, 0, 0)
            }
            binding.llResultContainer.addView(emptyView)
        } else {
            for (result in results) {
                addResultItem(result)
            }
        }
    }

    // ★★★ [핵심] 데이터를 가져오는 함수 (나중에 여기만 진짜 DB 코드로 바꾸면 됨) ★★★
    private fun fetchGroupSchedules(targetGroupId: Int): Pair<Int, Map<String, List<String>>> {
        // TODO: 나중에 Room DB나 Firebase에서 해당 그룹 멤버들의 일정을 조회하는 코드로 변경 예정

        // --- 지금은 가짜 데이터 (Mock Data) 리턴 ---
        val mockTotalMembers = 4
        val mockData = mapOf(
            "나(방장)" to listOf("13:00", "13:30", "14:00", "15:00"),
            "김철수" to listOf("13:00", "13:30", "16:00"),
            "이영희" to listOf("13:00", "14:00", "15:00"),
            "박민수" to listOf("14:00", "15:00")
        )

        return Pair(mockTotalMembers, mockData)
    }

    // 결과 아이템 하나를 화면에 붙이는 함수
    private fun addResultItem(result: GoldenTimeResult) {
        val itemView = layoutInflater.inflate(R.layout.item_golden_time, binding.llResultContainer, false)

        val tvTime = itemView.findViewById<TextView>(R.id.tvTimeInfo)
        val tvMembers = itemView.findViewById<TextView>(R.id.tvMemberInfo)
        val btnSelect = itemView.findViewById<TextView>(R.id.btnSelect)

        val timeString = "${result.startTime} ~ ${result.endTime}"
        tvTime.text = timeString

        val names = result.memberNames.joinToString(", ")
        tvMembers.text = "$names 가능 (${result.availableCount}/${result.totalMembers})"

        // 방장 권한 처리
        if (isLeader) {
            btnSelect.visibility = View.VISIBLE
            btnSelect.setOnClickListener {
                confirmTime(timeString)
            }
        } else {
            btnSelect.visibility = View.GONE
        }

        binding.llResultContainer.addView(itemView)
    }

    // 시간을 확정하는 함수 (DB 저장 + UI 갱신)
    private fun confirmTime(confirmedTimeStr: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            // 1. DB 업데이트
            if (groupId != -1) {
                val group = db.groupDao().getGroupById(groupId)
                if (group != null) {
                    group.confirmedTime = confirmedTimeStr
                    db.groupDao().update(group)
                }
            }

            // 2. UI 업데이트
            withContext(Dispatchers.Main) {
                Toast.makeText(this@GroupDetailActivity, "시간이 확정되었습니다! 🎉", Toast.LENGTH_SHORT).show()

                // 화면 정리 후 확정된 것만 보여주기
                binding.llResultContainer.removeAllViews()

                val finalView = layoutInflater.inflate(R.layout.item_golden_time, binding.llResultContainer, false)
                finalView.findViewById<TextView>(R.id.tvTimeInfo).apply {
                    text = confirmedTimeStr
                    setTextColor(android.graphics.Color.parseColor("#2D2FA8")) // 파란색 강조
                }
                finalView.findViewById<TextView>(R.id.tvMemberInfo).text = "최종 확정된 시간입니다."
                finalView.findViewById<TextView>(R.id.btnSelect).visibility = View.GONE // 버튼 숨김

                binding.llResultContainer.addView(finalView)
                binding.tvGoldenTimeLabel.text = "최종 확정 시간"
            }
        }
    }
}