package com.example.timecatch.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.timecatch.data.AppDatabase
import com.example.timecatch.data.UserEntity
import com.example.timecatch.databinding.ActivitySignUpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userDao = AppDatabase.getDatabase(this).userDao()

        binding.btnSignUp.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val pw1 = binding.etPassword.text.toString().trim()
            val pw2 = binding.etPassword2.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || pw1.isEmpty() || pw2.isEmpty()) {
                Toast.makeText(this, "빈칸부터 채워줘", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pw1 != pw2) {
                Toast.makeText(this, "비밀번호가 서로 달라", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!binding.cbAgree.isChecked) {
                Toast.makeText(this, "약관 동의 체크해줘", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val exists = withContext(Dispatchers.IO) {
                        userDao.isEmailExists(email)
                    }

                    if (exists) {
                        Toast.makeText(this@SignUpActivity, "이미 가입된 이메일이야", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    withContext(Dispatchers.IO) {
                        userDao.insertUser(
                            UserEntity(
                                name = name,
                                email = email,
                                password = pw1
                            )
                        )
                    }

                    Toast.makeText(this@SignUpActivity, "회원가입 완료! 로그인 해줘", Toast.LENGTH_SHORT).show()

                    // 회원가입 완료 후 -> 로그인으로 복귀
                    val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()

                } catch (e: Exception) {
                    Toast.makeText(this@SignUpActivity, "회원가입 에러: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvGoLogin.setOnClickListener {
            finish() // 그냥 뒤로(로그인) 돌아가도 됨
        }
    }
}
