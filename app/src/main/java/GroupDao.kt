package com.example.timecatch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update  // ★ [1] 이게 추가되어야 합니다!
import androidx.room.OnConflictStrategy

@Dao
interface GroupDao {
    // ★ [2] 테이블 이름을 'group_table'로 통일했습니다 (Entity와 맞춰야 함)
    @Query("SELECT * FROM group_table")
    fun getAllGroups(): List<Group>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroup(group: Group)

    // ★ [2] 여기도 groups -> group_table로 수정
    @Query("DELETE FROM group_table")
    fun deleteAll()

    // ID로 그룹 하나만 쏙 가져오기
    @Query("SELECT * FROM group_table WHERE id = :id LIMIT 1")
    fun getGroupById(id: Int): Group?

    // 정보 수정하기 (확정 시간 저장용)
    @Update
    fun update(group: Group)
}