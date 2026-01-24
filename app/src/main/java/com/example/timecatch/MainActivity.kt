package com.example.timecatch

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecatch.databinding.ActivityMainBinding
import com.example.timecatch.data.AppDatabase

class MainActivity : AppCompatActivity() {

    // 1. 변수 선언
    private lateinit var binding: ActivityMainBinding
    private val viewModel: HomeViewModel by viewModels() // ViewModel 연결
    private lateinit var groupAdapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. 뷰 바인딩 설정
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. 초기화 함수들 실행
        setupRecyclerView()
        observeData()
        setupListeners()

        // ❌ 여기서 데이터 불러오던 코드는 지웠습니다! (onResume으로 이동)
    }

    // ✅ [추가된 부분] 화면이 다시 보일 때마다 실행되는 함수
    override fun onResume() {
        super.onResume()
        // 그룹 만들고 돌아왔을 때 목록을 최신으로 갱신해줍니다!
        viewModel.fetchMyGroups()
    }

    // 리사이클러뷰(그룹 목록) 설정
    // MainActivity.kt 안에 있는 함수입니다.
    private fun setupRecyclerView() {
        groupAdapter = GroupAdapter { selectedGroup ->
            // 그룹 아이템을 클릭했을 때 실행되는 코드
            val intent = Intent(this, GroupDetailActivity::class.java)

            // 상세 화면에 데이터 넘겨주기 (택배 보내기 📦)
            intent.putExtra("GROUP_NAME", selectedGroup.groupName)
            intent.putExtra("GROUP_DATE", selectedGroup.targetDate)
            intent.putExtra("GROUP_ID", selectedGroup.id) // DB ID

            startActivity(intent)
        }

        binding.rvGroupList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = groupAdapter
        }
    }

    // 데이터 관찰 (목록 갱신)
    private fun observeData() {
        viewModel.myGroups.observe(this) { groups ->
            groupAdapter.submitList(groups)
        }
    }

    // 버튼 클릭 이벤트 모음
    private fun setupListeners() {
        // 1. 내 정보 아이콘
        binding.ivMyProfile.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        // 2. 내 일정 관리 버튼
        binding.btnManageSchedule.setOnClickListener {
            val intent = Intent(this, MyScheduleActivity::class.java)
            startActivity(intent)
        }

        // 3. 그룹 생성 버튼
        binding.btnCreateGroup.setOnClickListener {
            val intent = Intent(this, GroupCreateActivity::class.java)
            startActivity(intent)
        }
    }
}