package com.example.timecatch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.timecatch.Group
import com.example.timecatch.GroupDao
import com.example.timecatch.ScheduleEntity

@Database(
    entities = [ScheduleEntity::class, Group::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scheduleDao(): ScheduleDao
    abstract fun groupDao(): GroupDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "timecatch_db"
                )
                    // 엔티티/컬럼 바뀌었는데 version 올리는 걸 깜빡하면 크래시 나니까 방지용(개발 단계에서 편함)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
