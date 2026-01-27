package com.example.timecatch

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.timecatch.data.AppDatabase
import com.example.timecatch.databinding.ActivityGroupDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import android.widget.TimePicker // 이게 반드시 있어야 합니다.

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupDetailBinding
    private lateinit var db: AppDatabase
    private var groupId: Int = -1
    private var isLeader = false // DB 데이터에 따라 변경됨

    // 현재 사용자 ID (로그인 시 저장해둔 값이라고 가정, Intent로 받아야 함)
    private var currentUserId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setBackgroundDrawableResource(android.R.color.transparent)

        db = AppDatabase.getDatabase(this)

        // Intent 데이터 받기
        val groupName = intent.getStringExtra("GROUP_NAME") ?: "이름 없음"
        groupId = intent.getIntExtra("GROUP_ID", -1)

        // ★ 메인이나 로그인 화면에서 넘어올 때 USER_ID를 꼭 줘야 함! (임시로 1L 등 설정 가능)
        currentUserId = intent.getLongExtra("USER_ID", 1L)

        binding.tvGroupName.text = groupName
        binding.btnClose.setOnClickListener { finish() }

        // 로딩 시작
        loadDataAndShowGoldenTime()
    }


    private fun loadDataAndShowGoldenTime() {
        lifecycleScope.launch(Dispatchers.IO) {
            // 1. 그룹 정보 가져오기
            val group = db.groupDao().getGroupById(groupId) ?: return@launch

            // (테스트용) 방장 권한 true
            isLeader = true

            // 멤버 ID 파싱
            val memberIds = group.memberUserIds
                .split(",")
                .filter { it.isNotEmpty() }
                .map { it.toLong() }

            val memberAvailabilities = mutableMapOf<String, List<String>>()

            for (memberId in memberIds) {
                // 이름 가져오기
                val name = db.userDao().getUserName(memberId) ?: "멤버 $memberId"

                // ==========================================================
                // ★ [여기가 핵심] "2026년 1월 28일"을 숫자 날짜로 자동 변환!
                // ==========================================================
                val rawDate = group.targetDate // "2026년 1월 28일"
                val searchDates = mutableListOf<String>()

                // 1. 숫자만 추출하는 정규식 (년, 월, 일 글자 떼기)
                val regex = Regex("(\\d+)[^0-9]+(\\d+)[^0-9]+(\\d+)")
                val match = regex.find(rawDate)

                if (match != null) {
                    val (yStr, mStr, dStr) = match.destructured
                    val y = yStr.toInt()
                    val m = mStr.toInt()
                    val d = dStr.toInt()

                    // DB에 저장될 수 있는 모든 경우의 수 생성
                    searchDates.add(String.format("%d-%02d-%02d", y, m, d)) // 2026-01-28
                    searchDates.add("$y-$m-$d")                             // 2026-1-28
                    searchDates.add(String.format("%d.%02d.%02d", y, m, d)) // 2026.01.28
                    searchDates.add("$y.$m.$d")                             // 2026.1.28
                } else {
                    // 정규식 실패 시 원본이라도 넣기
                    searchDates.add(rawDate)
                }

                // 2. 만든 날짜들로 DB 싹 다 뒤지기
                val allSchedules = mutableListOf<com.example.timecatch.data.ScheduleEntity>()
                for (dateStr in searchDates) {
                    val schedules = db.scheduleDao().getSchedulesByDate(dateStr, memberId)
                    allSchedules.addAll(schedules)
                }
                // ==========================================================

                // 3. "가능한 시간"으로 변환 (중복 제거 포함)
                // (여러 포맷으로 검색하다 보면 같은 스케줄이 중복될 수 있으니 distinct() 처리)
                val distinctSchedules = allSchedules.distinctBy { it.id }
                val availableSlots = convertBusyToAvailable(distinctSchedules)
                memberAvailabilities[name] = availableSlots
            }

            // [STEP 3] 알고리즘 가동
            val results = GoldenTimeFinder.analyze(memberIds.size, memberAvailabilities)

            // [STEP 4] 결과 화면에 보여주기
            withContext(Dispatchers.Main) {
                updateResultUI(results)
            }
        }
    }

    // ★ 스케줄(Busy) 데이터를 받아서 -> 가능한 시간(Available) 리스트로 변환하는 함수
    private fun convertBusyToAvailable(schedules: List<com.example.timecatch.data.ScheduleEntity>): List<String> {
        // 하루 24시간을 30분 단위 48개 슬롯으로 표현 (true: 가능, false: 바쁨)
        val isAvailable = BooleanArray(48) { true }

        for (schedule in schedules) {
            val startIdx = timeStringToSlotIndex(schedule.startTime)
            val endIdx = timeStringToSlotIndex(schedule.endTime)

            // 스케줄 있는 시간대를 false(불가능)로 마킹
            for (i in startIdx until endIdx) {
                if (i in 0 until 48) {
                    isAvailable[i] = false
                }
            }
        }

        // true(가능)인 슬롯만 뽑아서 "13:00", "13:30" 형태의 문자열 리스트로 반환
        val availableTimeStrings = mutableListOf<String>()
        for (i in 0 until 48) {
            if (isAvailable[i]) {
                availableTimeStrings.add(slotIndexToTimeString(i))
            }
        }
        return availableTimeStrings
    }

    // UI 그리는 부분 (기존 코드 활용)
    private fun updateResultUI(results: List<GoldenTimeResult>) {
        binding.llResultContainer.removeAllViews()

        if (results.isEmpty()) {
            val emptyView = TextView(this).apply {
                text = "모두가 가능한 시간이 없습니다 ㅠㅠ"
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

    private fun addResultItem(result: GoldenTimeResult) {
        val itemView = layoutInflater.inflate(R.layout.item_golden_time, binding.llResultContainer, false)

        val tvTime = itemView.findViewById<TextView>(R.id.tvTimeInfo)
        val tvMembers = itemView.findViewById<TextView>(R.id.tvMemberInfo)
        val btnSelect = itemView.findViewById<TextView>(R.id.btnSelect)

        val timeString = "${result.startTime} ~ ${result.endTime}"
        tvTime.text = timeString

        val names = result.memberNames.joinToString(", ")
        tvMembers.text = "$names 가능 (${result.availableCount}/${result.totalMembers})"

        if (isLeader) {
            btnSelect.visibility = View.VISIBLE
            btnSelect.setOnClickListener {
                // ★ 바로 확정하지 않고 구체적인 시간을 정하는 다이얼로그를 띄웁니다.
                showPreciseTimePicker(result)
            }
        } else {
            btnSelect.visibility = View.GONE
        }

        binding.llResultContainer.addView(itemView)
    }

    private fun confirmTime(confirmedTimeStr: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val group = db.groupDao().getGroupById(groupId)
            if (group != null) {
                group.confirmedTime = confirmedTimeStr
                db.groupDao().update(group)
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(this@GroupDetailActivity, "시간 확정 완료!", Toast.LENGTH_SHORT).show()
                binding.tvGoldenTimeLabel.text = "최종 확정 시간"
                binding.llResultContainer.removeAllViews()

                // 확정된 뷰 하나만 다시 그림
                val finalView = layoutInflater.inflate(R.layout.item_golden_time, binding.llResultContainer, false)
                finalView.findViewById<TextView>(R.id.tvTimeInfo).text = confirmedTimeStr
                finalView.findViewById<TextView>(R.id.tvTimeInfo).setTextColor(Color.parseColor("#2D2FA8"))
                finalView.findViewById<TextView>(R.id.tvMemberInfo).text = "모임 시간이 확정되었습니다."
                finalView.findViewById<TextView>(R.id.btnSelect).visibility = View.GONE
                binding.llResultContainer.addView(finalView)
            }
        }
    }

    // --- 시간 변환 유틸 함수들 (GoldenTimeFinder에 있는 것과 동일 로직) ---
    private fun timeStringToSlotIndex(time: String): Int {
        val parts = time.split(":")
        val h = parts[0].toInt()
        val m = parts[1].toInt()
        return h * 2 + (if (m >= 30) 1 else 0)
    }

    private fun slotIndexToTimeString(index: Int): String {
        val h = index / 2
        val m = (index % 2) * 30
        return String.format("%02d:%02d", h, m)
    }

    private fun showPreciseTimePicker(result: GoldenTimeResult) {
        val view = layoutInflater.inflate(R.layout.dialog_time_picker, null)
        val tpClock = view.findViewById<TimePicker>(R.id.tpClock)
        val tvHint = view.findViewById<TextView>(R.id.tvDialogHint)

        tvHint.text = "범위: ${result.startTime} ~ ${result.endTime}"

        // 초기 시간 설정
        tpClock.hour = result.startTime.split(":")[0].toInt()
        tpClock.minute = 0

        // ★ 실시간 범위 검증 (시계 바늘 제어)
        tpClock.setOnTimeChangedListener { _, hour, minute ->
            val selectedTime = String.format("%02d:%02d", hour, minute)

            // 이 검증 로직이 스피너 휠을 강제로 제어합니다.
            if (selectedTime < result.startTime) {
                tpClock.hour = result.startTime.split(":")[0].toInt()
                tpClock.minute = result.startTime.split(":")[1].toInt()
            } else if (selectedTime > result.endTime) {
                tpClock.hour = result.endTime.split(":")[0].toInt()
                tpClock.minute = result.endTime.split(":")[1].toInt()
            }
        }

        // 다이얼로그 생성 및 실행
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("확정") { _, _ ->
                val confirmedTime = String.format("%02d:%02d", tpClock.hour, tpClock.minute)
                confirmTime(confirmedTime) // 최종 확정 로직 실행
            }
            .setNegativeButton("취소", null)
            .show()
    }
}