package com.example.timecatch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    suspend fun isEmailExists(email: String): Boolean

    @Query("SELECT * FROM users WHERE email = :email AND password = :pw LIMIT 1")
    suspend fun login(email: String, pw: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    // ★ 추가: 이메일 -> userId (GroupCreateActivity에서 Thread 안에서 쓰기 편하게 동기 함수로)
    @Query("SELECT id FROM users WHERE email = :email LIMIT 1")
    fun getUserIdByEmailSync(email: String): Long?
}
