package com.example.timecatch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.timecatch.Group // Group 클래스 위치에 맞춰 수정 필요
import com.example.timecatch.GroupDao
// import net.sqlcipher.database.SupportFactory // SQLCipher 사용 시 주석 해제

@Database(
    entities = [
        Group::class,
        UserEntity::class
    ],
    version = 2, // UserEntity가 포함된 높은 버전 선택
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun groupDao(): GroupDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "timecatch.db"
                )
                    // 1. 해커톤 꿀팁: DB 구조(Entity)가 바뀔 때 앱 삭제 없이 바로 반영함
                    .fallbackToDestructiveMigration()

                    // 2. 테스트 편의성: 시연 영상 찍을 때 Coroutine 처리가 덜 되었어도 앱이 안 멈추게 함
                    .allowMainThreadQueries()

                    // 3. 보안 전공자 포인트: SQLCipher 적용 시 아래 한 줄 추가 (비밀번호 설정)
                    // .openHelperFactory(SupportFactory("your-password".toByteArray()))

                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}