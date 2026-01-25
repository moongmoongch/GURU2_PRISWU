package com.example.timecatch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
// import com.example.timecatch.data.ScheduleEntity // 같은 패키지면 생략 가능

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules WHERE date = :date AND userId = :userId ORDER BY startTime ASC")
    fun getSchedulesByDate(date: String, userId: Long): List<ScheduleEntity>

    @Insert
    fun insert(schedule: ScheduleEntity)

    @Delete
    fun delete(schedule: ScheduleEntity)
}