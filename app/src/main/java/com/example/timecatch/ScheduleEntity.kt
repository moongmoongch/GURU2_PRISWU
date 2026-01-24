package com.example.timecatch

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val date: String,      // "yyyy-MM-dd"
    val title: String,
    val startTime: String, // "HH:mm"
    val endTime: String    // "HH:mm"
)

