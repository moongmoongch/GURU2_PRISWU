package com.example.timecatch

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecatch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var groupAdapter: GroupAdapter

    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getLongExtra("USER_ID", -1L)
        if (userId == -1L) {
            // 로그인 정보가 없으면 앱 흐름상 다시 로그인으로 보내는 게 맞지만,
            // 여기서는 최소한 종료 처리
            finish()
            return
        }

        setupRecyclerView()
        observeData()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchMyGroups(userId) // ★ 내 id로 필터된 그룹만 로드
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
        binding.ivMyProfile.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.btnManageSchedule.setOnClickListener {
            val intent = Intent(this, ScheduleManageActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        binding.btnCreateGroup.setOnClickListener {
            val intent = Intent(this, GroupCreateActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
    }
}