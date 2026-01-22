package com.example.timecatch  // <--- 이거 확인!

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecatch.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels() // ViewModel 연결
    private lateinit var groupAdapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeData()
        setupListeners()

        // 데이터 불러오기 요청
        viewModel.fetchMyGroups()
    }

    private fun setupRecyclerView() {
        // 어댑터 생성 (클릭 시 동작 정의)
        groupAdapter = GroupAdapter { selectedGroup ->
            // TODO: 그룹 클릭 시 '최적 시간 결과(Golden Time)' 화면으로 이동
            // val intent = Intent(this, ResultActivity::class.java)
            // intent.putExtra("groupId", selectedGroup.groupId)
            // startActivity(intent)
        }

        binding.rvGroupList.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = groupAdapter
        }
    }

    private fun observeData() {
        // ViewModel의 데이터가 바뀌면(Firestore 업데이트 등) 자동으로 UI 갱신
        viewModel.groupList.observe(this) { groups ->
            groupAdapter.submitList(groups)
        }
    }

    private fun setupListeners() {
        // 그룹 생성 버튼 클릭
        binding.btnCreateGroup.setOnClickListener {
            // 다른 팀원이 담당한 '그룹 생성 화면'으로 이동
            // val intent = Intent(this, CreateGroupActivity::class.java)
            // startActivity(intent)
        }
    }
}