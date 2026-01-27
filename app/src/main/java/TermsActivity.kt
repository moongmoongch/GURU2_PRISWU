package com.example.timecatch.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timecatch.databinding.ActivityTermsBinding

class TermsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTermsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        // 간략 이용약관(시연/해커톤용 버전)
        binding.tvTermsBody.text = """
[TimeCatch 이용약관(요약)]

1. 목적
- 본 약관은 TimeCatch 서비스(이하 “서비스”) 이용과 관련하여 사용자와 서비스 제공자 간 권리/의무를 정합니다.

2. 계정 및 가입
- 사용자는 정확한 정보를 입력해야 합니다.
- 타인의 정보를 도용하거나 부정확한 정보를 입력한 경우 서비스 이용이 제한될 수 있습니다.

3. 서비스 내용
- 서비스는 일정 등록/조회, 그룹 생성 및 일정 조율 기능을 제공합니다.
- 서비스 기능은 개선/변경될 수 있습니다.

4. 사용자 의무
- 사용자는 법령 및 본 약관을 준수해야 합니다.
- 서비스 운영을 방해하는 행위(과도한 요청, 비정상 사용 등)는 제한될 수 있습니다.

5. 개인정보 및 보안(요약)
- 서비스 제공을 위해 최소한의 정보(예: 이름, 아이디, 비밀번호)를 처리합니다.
- 사용자는 비밀번호를 포함한 계정정보를 안전하게 관리해야 합니다.

6. 책임 제한(요약)
- 서비스는 기능 제공을 위해 노력하지만, 네트워크/기기 오류 등 불가항력 사유로 인한 손해에 대해 제한적으로 책임을 집니다.

7. 문의
- 약관/서비스 관련 문의는 앱 내 안내 또는 운영자 채널을 통해 진행합니다.

(이 문서는 시연/프로젝트용 요약이며, 운영 단계에서는 정식 약관으로 대체 권장)
        """.trimIndent()
    }
}
