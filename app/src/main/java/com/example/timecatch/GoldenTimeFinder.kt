package com.example.timecatch.com.example.timecatch

import kotlin.collections.iterator

// 1. 결과 데이터를 담을 그릇 (화면에 보여줄 정보)
data class GoldenTimeResult(
    val startTime: String,   // 시작 시간 (예: "13:00")
    val endTime: String,     // 종료 시간 (예: "15:00")
    val availableCount: Int, // 가능한 인원 수 (예: 4)
    val totalMembers: Int,   // 전체 인원 수 (예: 4)
    val memberNames: List<String> // 가능한 멤버 이름 리스트
) : Comparable<GoldenTimeResult> {

    // 정렬 기준: 1. 인원수 많은 순 -> 2. 시간 긴 순 -> 3. 빠른 시간 순
    override fun compareTo(other: GoldenTimeResult): Int {
        if (this.availableCount != other.availableCount) {
            return other.availableCount - this.availableCount // 인원수 내림차순
        }
        // (지속 시간 비교 로직은 생략, 필요시 추가 가능)
        return 0
    }
}

// 2. 알고리즘 본체
object GoldenTimeFinder {

    // 하루를 30분 단위로 쪼갠 슬롯 개수 (24시간 * 2 = 48슬롯)
    private const val TOTAL_SLOTS = 48

    /**
     * @param memberAvailabilities: 멤버별 가능 시간 데이터 리스트
     * 예: [ {"홍길동", ["13:00", "13:30", "14:00"]}, {"김철수", ["13:00", "13:30"]} ]
     */
    fun analyze(
        totalMemberCount: Int,
        memberAvailabilities: Map<String, List<String>> // Key: 이름, Value: 가능한 시간 슬롯들("13:00")
    ): List<GoldenTimeResult> {

        // 1단계: 빈 시간표 만들기 (0으로 채워진 48개 칸)
        // 각 칸에는 {카운트, 가능한 사람 명단}을 저장함
        val timeSlots = Array(TOTAL_SLOTS) {
            mutableSetOf<String>() // 이 시간에 가능한 사람들의 이름을 저장할 Set
        }

        // 2단계: 투표하기 (Stacking)
        // 모든 멤버의 가능 시간을 돌면서 해당 슬롯에 이름표를 넣음
        for ((name, times) in memberAvailabilities) {
            for (time in times) {
                val slotIndex = timeToSlotIndex(time)
                if (slotIndex in 0 until TOTAL_SLOTS) {
                    timeSlots[slotIndex].add(name)
                }
            }
        }

        // 3단계: 연속된 시간 뭉치기 (Grouping)
        // 뚝뚝 끊어진 30분짜리들을 이어서 "13:00 ~ 15:00 (2시간)" 형태로 만듦
        val results = mutableListOf<GoldenTimeResult>()
        var currentStartSlot = -1
        var currentMembers = mutableSetOf<String>()

        for (i in 0 until TOTAL_SLOTS) {
            val slotMembers = timeSlots[i]

            // 새로운 시간 블록의 시작
            if (currentStartSlot == -1 && slotMembers.isNotEmpty()) {
                currentStartSlot = i
                currentMembers = slotMembers.toMutableSet()
            }
            // 블록이 이어지는 중 (멤버 구성이 같아야 같은 블록으로 침)
            else if (currentStartSlot != -1) {
                if (slotMembers != currentMembers) {
                    // 멤버 구성이 달라지거나 끊기면 -> 방금까지 뭉친거 저장!
                    results.add(
                        createResult(currentStartSlot, i, currentMembers, totalMemberCount)
                    )

                    // 다시 시작 (만약 현재 슬롯도 비어있지 않다면)
                    if (slotMembers.isNotEmpty()) {
                        currentStartSlot = i
                        currentMembers = slotMembers.toMutableSet()
                    } else {
                        currentStartSlot = -1
                        currentMembers = mutableSetOf()
                    }
                }
            }
        }

        // 마지막 남은 블록 처리
        if (currentStartSlot != -1) {
            results.add(createResult(currentStartSlot, TOTAL_SLOTS, currentMembers, totalMemberCount))
        }

        // 4단계: 순위 매기기 (Ranking) & 필터링
        // 최소 2명 이상 모이는 시간만 추천 + 정렬
        return results.filter { it.availableCount >= 2 }.sorted()
    }

    // 보조 함수: "13:30" -> 슬롯 번호 27 로 변환
    private fun timeToSlotIndex(timeStr: String): Int {
        val parts = timeStr.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        return hour * 2 + (if (minute >= 30) 1 else 0)
    }

    // 보조 함수: 슬롯 번호 -> "13:30" 로 변환
    private fun slotIndexToTime(index: Int): String {
        val hour = index / 2
        val minute = (index % 2) * 30
        return String.format("%02d:%02d", hour, minute)
    }

    // 결과 객체 생성 도우미
    private fun createResult(startIdx: Int, endIdx: Int, members: Set<String>, total: Int): GoldenTimeResult {
        return GoldenTimeResult(
            startTime = slotIndexToTime(startIdx),
            endTime = slotIndexToTime(endIdx),
            availableCount = members.size,
            totalMembers = total,
            memberNames = members.toList()
        )
    }
}