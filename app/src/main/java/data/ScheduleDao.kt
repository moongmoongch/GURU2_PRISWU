package com.example.timecatch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.timecatch.ScheduleEntity

@Dao
interface ScheduleDao {

    // 일정 추가
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity)

    // 특정 날짜 일정 조회
    @Query("SELECT * FROM schedules WHERE date = :date ORDER BY startTime ASC")
    suspend fun getSchedulesByDate(date: String): List<ScheduleEntity>

    // 일정 삭제
    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteSchedule(id: Long)
}
