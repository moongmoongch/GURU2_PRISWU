package com.example.timecatch

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_table")
data class Group(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // 누가 만든 그룹인지(방장)
    val ownerUserId: Long,

    val groupName: String,
    val targetDate: String,

    // 화면 표시용 / 초대 기록용 (이메일 기반으로 입력 받는다고 가정)
    val invitedEmails: String,

    // 멤버 userId들을 ",1,2,3," 형태로 저장 (검색/포함 여부 체크용)
    val memberUserIds: String,

    // 확정된 시간
    var confirmedTime: String? = null
)