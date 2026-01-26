package com.example.timecatch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.timecatch.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)

    private val _myGroups = MutableLiveData<List<Group>>()
    val myGroups: LiveData<List<Group>> get() = _myGroups

    fun fetchMyGroups(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val groups = try {
                db.groupDao().getGroupsForUser(userId)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }

            withContext(Dispatchers.Main) {
                _myGroups.value = groups
            }
        }
    }
    fun deleteGroup(groupId: Int, currentUserId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. DB에서 삭제
            db.groupDao().deleteGroupById(groupId)

            // 2. 삭제 후 목록 갱신 (UI 업데이트)
            // (이미 fetchMyGroups 함수가 있으므로 재사용)
            fetchMyGroups(currentUserId)
        }
    }
}
