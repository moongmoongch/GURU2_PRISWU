package com.example.timecatch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Long,      // UserEntity의 id와 타입을 맞춥니다
    val date: String,
    val title: String,
    val startTime: String,
    val endTime: String
)