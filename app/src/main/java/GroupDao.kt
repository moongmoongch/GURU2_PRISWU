package com.example.timecatch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface GroupDao {

    // 전체 조회(필요하면 유지) - 지금 홈에서는 안 쓰게 됨
    @Query("SELECT * FROM group_table")
    fun getAllGroups(): List<Group>

    // ★ 홈 화면용: userId가 포함된 그룹만 가져오기
    @Query("""
        SELECT * FROM group_table
        WHERE memberUserIds LIKE '%,' || :userId || ',%'
        ORDER BY id DESC
    """)
    fun getGroupsForUser(userId: Long): List<Group>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroup(group: Group)

    @Query("DELETE FROM group_table")
    fun deleteAll()

    @Query("SELECT * FROM group_table WHERE id = :id LIMIT 1")
    fun getGroupById(id: Int): Group?

    @Update
    fun update(group: Group)
}
