package com.example.timecatch

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MyPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_page)

        // 시스템바(상태바, 네비게이션바) 영역만큼 패딩 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. 초기 데이터 설정 (나중에 다른 팀원이 만든 DB/SharedPrefs와 연결할 부분)
        val currentUserName = "박슈니" // 실제 본인 성함으로 변경 가능
        val currentUserId = "parkswuni" // 실제 아이디로 변경 가능

        // 2. View 객체 참조 (XML의 android:id와 일치해야 함)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvName = findViewById<TextView>(R.id.tvMyName)
        val tvId = findViewById<TextView>(R.id.tvMyId)
        val tvLogout = findViewById<TextView>(R.id.tvLogout)

        // 3. 데이터 반영
        tvName.text = currentUserName
        tvId.text = "@$currentUserId" // @를 포함하여 아이디 표시

        // 4. 리스너 설정

        // 뒤로가기 버튼: 현재 액티비티 종료
        btnBack.setOnClickListener {
            finish()
        }

        // 로그아웃 버튼: 알림창 표시 후 종료 (나중에 세션 삭제 로직 추가 예정)
        tvLogout.setOnClickListener {
            Toast.makeText(this, "성공적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
            // 로그인 화면으로 이동하는 로직이 필요하다면 여기에 Intent 추가
            finish()
        }
    }
}