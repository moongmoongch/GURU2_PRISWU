package com.example.timecatch  // <--- 이거 확인!

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Group::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "timecatch_database" // DB 파일 이름
                ).allowMainThreadQueries() // ★주의: 원래는 안 되지만, 지금은 테스트라 허용함
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}