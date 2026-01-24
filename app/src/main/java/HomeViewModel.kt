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

    private val _groupList = MutableLiveData<List<Group>>()
    val groupList: LiveData<List<Group>> get() = _groupList

    init {
        fetchMyGroups()
    }

    fun fetchMyGroups() {
        viewModelScope.launch {
            val groups = withContext(Dispatchers.IO) {
                val dao = db.groupDao()
                var savedGroups = dao.getAllGroups()

                if (savedGroups.isEmpty()) {
                    dao.insertGroup(Group(groupName = "데이터베이스 팀플", targetDate = "1월 12일"))
                    dao.insertGroup(Group(groupName = "가족 외식", targetDate = "2월 24일"))
                    dao.insertGroup(Group(groupName = "보안 동아리 회의", targetDate = null))
                    savedGroups = dao.getAllGroups()
                }
                savedGroups
            }
            _groupList.value = groups
        }
    }
}

