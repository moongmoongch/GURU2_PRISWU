package com.example.timecatch

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.timecatch.data.AppDatabase
import com.example.timecatch.ui.auth.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userId = intent.getLongExtra("USER_ID", -1L)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvName = findViewById<TextView>(R.id.tvMyName)
        val tvId = findViewById<TextView>(R.id.tvMyId)
        val tvLogout = findViewById<TextView>(R.id.tvLogout)

        if (userId != -1L) {
            val db = AppDatabase.getDatabase(this)
            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) {
                    db.userDao().getUserById(userId)
                }

                user?.let {
                    tvName.text = it.name
                    tvId.text = it.email
                }
            }
        }

        btnBack.setOnClickListener {
            finish()
        }

        // ★ 로그아웃 클릭 리스너 수정
        tvLogout.setOnClickListener {
            Toast.makeText(this, "성공적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show()

            // 1. 로그인 화면으로 이동하는 Intent 생성
            val intent = Intent(this, LoginActivity::class.java)

            // 2. 보안을 위해 현재까지 쌓인 모든 액티비티 스택을 제거합니다.
            // FLAG_ACTIVITY_NEW_TASK: 새로운 태스크 생성
            // FLAG_ACTIVITY_CLEAR_TASK: 기존 태스크 내의 모든 액티비티 삭제
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            // 3. 현재 마이페이지도 종료
            finish()
        }
    }
}