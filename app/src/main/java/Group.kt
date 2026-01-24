package com.example.timecatch

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_table")
data class Group(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // 혹시 uid로 되어있으면 그대로 두세요
    val groupName: String,
    val targetDate: String,
    val invitedIds: String, // List를 String으로 변환해서 저장했었죠?

    // ▼▼▼ [추가] 확정된 시간을 저장할 변수 (처음엔 null) ▼▼▼
    var confirmedTime: String? = null
)