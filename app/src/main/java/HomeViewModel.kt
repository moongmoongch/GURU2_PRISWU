package com.example.timecatch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// Firebase 관련 import가 있으면 다 지워집니다.
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application) // Room DB 연결
    private val _groupList = MutableLiveData<List<Group>>()
    val groupList: LiveData<List<Group>> get() = _groupList

    init {
        fetchMyGroups()
    }

    fun fetchMyGroups() {
        // Firebase 코드(db.collection...)는 다 지우고 아래 로직만 남깁니다.
        var savedGroups = db.groupDao().getAllGroups()

        if (savedGroups.isEmpty()) {
            // 테스트용 가짜 데이터
            db.groupDao().insertGroup(Group(groupName = "데이터베이스 팀플", targetDate = "1월 12일"))
            db.groupDao().insertGroup(Group(groupName = "가족 외식", targetDate = "2월 24일"))
            db.groupDao().insertGroup(Group(groupName = "보안 동아리 회의", targetDate = null))

            savedGroups = db.groupDao().getAllGroups()
        }

        _groupList.value = savedGroups
    }
}