package com.example.timecatch.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.timecatch.MainActivity
import com.example.timecatch.data.AppDatabase
import com.example.timecatch.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userDao = AppDatabase.getDatabase(this).userDao()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pw = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "이메일/비번을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val user = withContext(Dispatchers.IO) {
                        userDao.login(email, pw)
                    }

                    if (user != null) {
                        Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "로그인 실패 (정보 확인)", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "로그인 에러: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnGoSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
