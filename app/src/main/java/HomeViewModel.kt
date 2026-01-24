package com.example.timecatch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // 1. 내부에서만 수정 가능한 MutableLiveData
    private val _myGroups = MutableLiveData<List<Group>>()

    // 2. 외부(MainActivity)에서 관찰만 가능한 LiveData
    val myGroups: LiveData<List<Group>> = _myGroups

    fun fetchMyGroups() {
        viewModelScope.launch {
            // TODO: Room DB에서 데이터를 가져오는 로직을 넣으세요.
            // 지금은 에러를 해결하기 위해 빈 리스트를 넣어줍니다.
            _myGroups.value = listOf()
        }
    }
}