package com.example.timecatch

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timecatch.databinding.ActivityGroupCreateBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GroupCreateActivity : AppCompatActivity() {

    private lateinit var b: ActivityGroupCreateBinding
    private lateinit var db: AppDatabase // 1. DB 변수 추가

    private var selectedDateMillis: Long? = null
    private val invitedIds = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        b = ActivityGroupCreateBinding.inflate(layoutInflater)
        setContentView(b.root)

        // 1. DB 연결 초기화
        db = AppDatabase.getDatabase(this)

        // ▼▼▼ 4. 뒤로가기 버튼은 '여기'에 넣어야 합니다! ▼▼▼
        // (변수 이름도 'binding'이 아니라 'b'로 바꿔주세요!)
        b.btnBack.setOnClickListener {
            finish()
        }

        // 초기값: 오늘 날짜
        selectedDateMillis = b.calendarView.date
        b.tvSelectedDate.text = "선택된 날짜: ${formatDateFull(selectedDateMillis!!)}"

        // 달력 날짜 선택 이벤트
        b.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            selectedDateMillis = cal.timeInMillis
            b.tvSelectedDate.text = "선택된 날짜: ${formatDateFull(cal.timeInMillis)}"


        }

        // 친구 추가 버튼
        b.btnAddFriend.setOnClickListener {
            val id = b.etFriendId.text.toString().trim()

            if (id.isBlank()) {
                toast("친구 아이디를 입력하세요.")
                return@setOnClickListener
            }
            if (invitedIds.contains(id)) {
                toast("이미 추가된 친구입니다.")
                return@setOnClickListener
            }

            invitedIds.add(id)
            b.etFriendId.setText("")
            renderInvitedList()
        }

        // ★★★ 그룹 생성 버튼 (여기가 핵심!) ★★★
        b.btnCreateGroup.setOnClickListener {
            // 1. 날짜 확인
            val dateMillis = selectedDateMillis ?: run {
                toast("날짜를 선택하세요.")
                return@setOnClickListener
            }

            // 2. 그룹 이름 확인 (XML에 etGroupName이 있어야 합니다!)
            // 만약 XML에 아직 입력창을 안 만드셨다면, 일단 주석 처리하고 "새로운 그룹"이라고 넣으셔도 됩니다.
            val groupName = b.etGroupName.text.toString().trim()

            if (groupName.isBlank()) {
                toast("그룹 이름을 입력해주세요.")
                return@setOnClickListener
            }

            // 3. 진짜 DB에 저장하기 (백그라운드 작업)
            Thread {
                val dateStr = formatDateFull(dateMillis)

                // 새 그룹 객체 만들기
                val newGroup = Group(
                    groupName = groupName,
                    targetDate = dateStr,

                    invitedIds = invitedIds.joinToString(",")
                )

                // DB 저장
                db.groupDao().insertGroup(newGroup)

                // 4. 저장 끝나면 화면 닫기 (메인으로 이동)
                runOnUiThread {
                    toast("그룹 생성 완료!\n($groupName - $dateStr)")
                    finish() // 현재 화면 닫기 -> onResume()이 불리며 메인 목록 갱신됨
                }
            }.start()
        }

        renderInvitedList()
    }

    private fun renderInvitedList() {
        b.tvInvitedList.text =
            if (invitedIds.isEmpty()) "(아직 없음)"
            else invitedIds.joinToString(", ")
    }

    private fun formatDateFull(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA)
        return sdf.format(Date(millis))
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}