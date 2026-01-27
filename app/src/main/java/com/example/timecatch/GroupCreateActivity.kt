package com.example.timecatch

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timecatch.databinding.ActivityGroupCreateBinding
import com.example.timecatch.data.AppDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GroupCreateActivity : AppCompatActivity() {

    private lateinit var b: ActivityGroupCreateBinding
    private lateinit var db: AppDatabase

    private var selectedDateMillis: Long? = null

    // 사용자가 입력하는 "친구 아이디"를 이메일로 취급
    private val invitedEmails = mutableListOf<String>()

    // 로그인한 내 userId
    private var ownerUserId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityGroupCreateBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = AppDatabase.getDatabase(this)

        ownerUserId = intent.getLongExtra("USER_ID", -1L)
        if (ownerUserId == -1L) {
            toast("로그인 정보가 없습니다. 다시 로그인해주세요.")
            finish()
            return
        }

        b.btnBack.setOnClickListener { finish() }

        // 초기값: 오늘 날짜
        selectedDateMillis = b.calendarView.date

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
        }

        // 친구 추가(이메일)
        b.btnAddFriend.setOnClickListener {
            val email = b.etFriendId.text.toString().trim()

            if (email.isBlank()) {
                toast("친구 이메일을 입력하세요.")
                return@setOnClickListener
            }
            if (invitedEmails.contains(email)) {
                toast("이미 추가된 친구입니다.")
                return@setOnClickListener
            }

            invitedEmails.add(email)
            b.etFriendId.setText("")
            renderInvitedList()
        }

        // 그룹 생성
        b.btnCreateGroup.setOnClickListener {
            val dateMillis = selectedDateMillis ?: run {
                toast("날짜를 선택하세요.")
                return@setOnClickListener
            }

            val groupName = b.etGroupName.text.toString().trim()
            if (groupName.isBlank()) {
                toast("그룹 이름을 입력해주세요.")
                return@setOnClickListener
            }

            Thread {
                val dateStr = formatDateFull(dateMillis)

                // 1) 초대 이메일 -> userId로 변환
                val memberIdSet = linkedSetOf<Long>()
                memberIdSet.add(ownerUserId) // 내 id는 무조건 포함

                val invalidEmails = mutableListOf<String>()
                for (email in invitedEmails) {
                    val uid = db.userDao().getUserIdByEmailSync(email)
                    if (uid == null) invalidEmails.add(email) else memberIdSet.add(uid)
                }

                // 2) 없는 유저(가입 안 된 이메일)가 있으면 생성 중단
                if (invalidEmails.isNotEmpty()) {
                    runOnUiThread {
                        toast("가입되지 않은 이메일이 있어 그룹을 생성할 수 없습니다:\n${invalidEmails.joinToString(", ")}")
                    }
                    return@Thread
                }

                // 3) memberUserIds를 ",1,2,3," 형태로 저장
                val memberUserIds = "," + memberIdSet.joinToString(",") + ","

                val newGroup = Group(
                    ownerUserId = ownerUserId,
                    groupName = groupName,
                    targetDate = dateStr,
                    invitedEmails = invitedEmails.joinToString(","),
                    memberUserIds = memberUserIds
                )

                db.groupDao().insertGroup(newGroup)

                runOnUiThread {
                    toast("그룹 생성 완료!\n($groupName - $dateStr)")
                    finish()
                }
            }.start()
        }

        renderInvitedList()
    }

    private fun renderInvitedList() {
        b.tvInvitedList.text =
            if (invitedEmails.isEmpty()) "(아직 없음)"
            else invitedEmails.joinToString(", ")
    }

    private fun formatDateFull(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA)
        return sdf.format(Date(millis))
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
