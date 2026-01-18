package com.example.timecatch

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // 이 'id'가 없어서 에러 났던 것임
    val groupName: String,
    val targetDate: String?,
    val memberCount: Int = 0
)