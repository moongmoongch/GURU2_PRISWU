package com.example.timecatch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.timecatch.Group
import com.example.timecatch.GroupDao

@Database(
    entities = [Group::class, UserEntity::class, ScheduleEntity::class],
    version = 3, // 버전업 필수
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun userDao(): UserDao
    abstract fun scheduleDao(): ScheduleDao // 추가

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "timecatch.db"
                ).fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // 해커톤 시연용
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}