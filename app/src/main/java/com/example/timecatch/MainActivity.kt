package com.example.timecatch

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecatch.databinding.ActivityMainBinding
import androidx.appcompat.app.AlertDialog // [추가] 다이얼로그용

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

    // [수정] 어댑터 초기화 부분 변경
    private fun setupRecyclerView() {
        // 어댑터 생성 시, 롱클릭했을 때 실행할 동작을 정의
        groupAdapter = GroupAdapter { groupId ->
            showDeleteDialog(groupId)
        }

        binding.rvGroupList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = groupAdapter
        }
    }

    private fun showDeleteDialog(groupId: Int) {
        AlertDialog.Builder(this)
            .setTitle("그룹 삭제")
            .setMessage("정말로 이 그룹을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                // 뷰모델에게 삭제 요청
                viewModel.deleteGroup(groupId, userId)
            }
            .setNegativeButton("취소", null)
            .show()
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