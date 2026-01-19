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

    private var selectedDateMillis: Long? = null
    private val invitedIds = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityGroupCreateBinding.inflate(layoutInflater)
        setContentView(b.root)

        // 초기값: 오늘 날짜(달력 기본값)
        selectedDateMillis = b.calendarView.date
        b.tvSelectedDate.text = "선택된 날짜: ${formatDateFull(selectedDateMillis!!)}"

        // 달력 날짜 선택 이벤트
        b.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month) // month는 0부터 시작(1월=0)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            selectedDateMillis = cal.timeInMillis
            b.tvSelectedDate.text = "선택된 날짜: ${formatDateFull(cal.timeInMillis)}"
        }

        // 친구 추가
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

        // 그룹 생성(로컬 데모)
        b.btnCreateGroup.setOnClickListener {
            val date = selectedDateMillis ?: run {
                toast("날짜를 선택하세요.")
                return@setOnClickListener
            }

            val dateStr = formatDateFull(date)
            val invitedStr = if (invitedIds.isEmpty()) "(없음)" else invitedIds.joinToString(", ")

            toast("그룹 생성!\n날짜: $dateStr\n초대: $invitedStr")
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
