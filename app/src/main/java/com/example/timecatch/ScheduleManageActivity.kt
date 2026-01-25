package com.example.timecatch

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timecatch.data.AppDatabase
import com.example.timecatch.data.ScheduleEntity
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ScheduleManageActivity : AppCompatActivity() {
    private lateinit var tvSelectedDate: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var db: AppDatabase
    private val cal = Calendar.getInstance()
    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

    private var currentLoggedInUserId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_manage)

        currentLoggedInUserId = intent.getLongExtra("USER_ID", -1L)

        if (currentLoggedInUserId == -1L) {
            Toast.makeText(this, "잘못된 접근입니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db = AppDatabase.getDatabase(this)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        recyclerView = findViewById(R.id.rvSchedules)

        // ★ 추가: 좌측 상단 뒤로가기(X) 버튼 로직
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish() // 현재 화면을 닫고 홈(MainActivity)으로 이동
        }

        scheduleAdapter = ScheduleAdapter { schedule ->
            deleteSchedule(schedule)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = scheduleAdapter

        findViewById<CalendarView>(R.id.calendarView).setOnDateChangeListener { _, y, m, d ->
            cal.set(y, m, d)
            val dateStr = fmt.format(cal.time)
            tvSelectedDate.text = "$dateStr 일정"
            loadSchedules(dateStr)
        }

        findViewById<Button>(R.id.btnAddSchedule).setOnClickListener { openAddScheduleBottomSheet() }
        loadSchedules(fmt.format(cal.time))
    }

    private fun loadSchedules(date: String) {
        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) {
                db.scheduleDao().getSchedulesByDate(date, currentLoggedInUserId)
            }
            scheduleAdapter.setItems(list)
        }
    }

    private fun openAddScheduleBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_add_schedule, null)
        dialog.setContentView(view)

        view.findViewById<ImageButton>(R.id.btnClose).setOnClickListener { dialog.dismiss() }

        view.findViewById<Button>(R.id.btnAdd).setOnClickListener {
            val title = view.findViewById<EditText>(R.id.etTitle).text.toString()
            val dateStr = fmt.format(cal.time)
            val tpStart = view.findViewById<TimePicker>(R.id.tpStart)
            val tpEnd = view.findViewById<TimePicker>(R.id.tpEnd)
            val startTime = String.format("%02d:%02d", tpStart.hour, tpStart.minute)
            val endTime = String.format("%02d:%02d", tpEnd.hour, tpEnd.minute)

            lifecycleScope.launch(Dispatchers.IO) {
                db.scheduleDao().insert(
                    ScheduleEntity(
                        userId = currentLoggedInUserId,
                        date = dateStr,
                        title = title,
                        startTime = startTime,
                        endTime = endTime
                    )
                )
                withContext(Dispatchers.Main) {
                    loadSchedules(dateStr)
                    dialog.dismiss()
                    Toast.makeText(this@ScheduleManageActivity, "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    private fun deleteSchedule(schedule: ScheduleEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.scheduleDao().delete(schedule)
            withContext(Dispatchers.Main) {
                loadSchedules(fmt.format(cal.time))
                Toast.makeText(this@ScheduleManageActivity, "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}