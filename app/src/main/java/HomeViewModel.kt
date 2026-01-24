package com.example.timecatch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.timecatch.data.AppDatabase
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val _myGroups = MutableLiveData<List<Group>>()
    val myGroups: LiveData<List<Group>> get() = _myGroups

    // 앱 켜질 때 자동 실행 아님! (MainActivity가 시킬 때만 함)
    // init { fetchMyGroups() }  <-- 이거 지움

    fun fetchMyGroups() {
        // 테스트 데이터 만드는 코드 다 삭제!
        // 오직 DB에서 가져오는 코드만 남김
        try {
            val savedGroups = db.groupDao().getAllGroups()
            _myGroups.value = savedGroups
        } catch (e: Exception) {
            e.printStackTrace()
            _myGroups.value = emptyList()
        }
    }
}