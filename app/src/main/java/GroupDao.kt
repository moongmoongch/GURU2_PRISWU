package com.example.timecatch  // <--- 이거 확인!

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups")
    fun getAllGroups(): List<Group>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroup(group: Group)

    @Query("DELETE FROM groups")
    fun deleteAll() // 테스트용 초기화
}