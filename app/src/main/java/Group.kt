package com.example.timecatch

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupName: String,
    val targetDate: String?,
    val memberCount: Int = 0
)