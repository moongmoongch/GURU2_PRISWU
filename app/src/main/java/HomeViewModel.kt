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
}
