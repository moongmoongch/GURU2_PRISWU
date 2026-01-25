package com.example.timecatch

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecatch.databinding.ActivityMainBinding
import com.example.timecatch.data.AppDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var groupAdapter: GroupAdapter

    // 1. 전달받은 유저 ID를 저장할 변수
    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. LoginActivity에서 보낸 USER_ID 추출
        userId = intent.getLongExtra("USER_ID", -1L)

        setupRecyclerView()
        observeData()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchMyGroups()
    }

    private fun setupRecyclerView() {
        groupAdapter = GroupAdapter()

        binding.rvGroupList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = groupAdapter
        }
    }

    private fun observeData() {
        viewModel.myGroups.observe(this) { groups ->
            groupAdapter.submitList(groups)
        }
    }

    private fun setupListeners() {
        // 1. 내 정보 아이콘
        binding.ivMyProfile.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            // 필요 시 마이페이지에도 ID 전달 가능
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        // 2. 내 일정 관리 버튼 (여기서 ID를 넘겨줘야 계정별 일정이 보입니다)
        binding.btnManageSchedule.setOnClickListener {
            val intent = Intent(this, ScheduleManageActivity::class.java)
            intent.putExtra("USER_ID", userId) // ScheduleManageActivity로 ID 전달
            startActivity(intent)
        }

        // 3. 그룹 생성 버튼
        binding.btnCreateGroup.setOnClickListener {
            val intent = Intent(this, GroupCreateActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
    }
}