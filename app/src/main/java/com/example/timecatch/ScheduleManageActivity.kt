package com.example.timecatch

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timecatch.data.AppDatabase
import com.example.timecatch.data.ScheduleDao
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleManageActivity : AppCompatActivity() {

    private lateinit var scheduleDao: ScheduleDao
    private lateinit var adapter: ScheduleAdapter

    private var selectedDate: String = "" // yyyy-MM-dd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_manage)

        // 1) Room DB 연결
        val db = AppDatabase.getDatabase(this)
        scheduleDao = db.scheduleDao()

        // 2) RecyclerView
        adapter = ScheduleAdapter { item ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    scheduleDao.deleteSchedule(item.id)
                }
                loadSchedules(selectedDate)
            }
        }

        val rv = findViewById<RecyclerView>(R.id.rvSchedules)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // 3) 초기 날짜 = 오늘
        val cal = Calendar.getInstance()
        selectedDate = formatDate(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        val tvSelectedDate = findViewById<TextView>(R.id.tvSelectedDate)
        tvSelectedDate.text = "$selectedDate 일정"

        // 4) 처음 로딩
        loadSchedules(selectedDate)

        // 5) 달력 변경 시 로딩
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = formatDate(year, month, dayOfMonth)
            tvSelectedDate.text = "$selectedDate 일정"
            loadSchedules(selectedDate)
        }

        // 6) 일정 추가 버튼
        findViewById<Button>(R.id.btnAddSchedule).setOnClickListener {
            openAddScheduleBottomSheet()
        }
    }

    private fun loadSchedules(date: String) {
        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) {
                scheduleDao.getSchedulesByDate(date)
            }
            adapter.submitList(list)
        }
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(cal.time)
    }

    private fun openAddScheduleBottomSheet() {
        val dialog = BottomSheetDialog(this)

        val view = LayoutInflater.from(this)
            .inflate(R.layout.bottom_sheet_add_schedule, null)
        dialog.setContentView(view)

        val btnClose = view.findViewById<TextView>(R.id.btnClose)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val tpStart = view.findViewById<TimePicker>(R.id.tpStart)
        val tpEnd = view.findViewById<TimePicker>(R.id.tpEnd)
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)

        // 24시간제 (XML에 is24HourView 쓰지 말고 여기서)
        tpStart.setIs24HourView(true)
        tpEnd.setIs24HourView(true)

        btnClose.setOnClickListener { dialog.dismiss() }

        btnAdd.setOnClickListener {
            val title = etTitle.text?.toString()?.trim().orEmpty()
            if (title.isBlank()) {
                etTitle.error = "일정 이름을 입력해 주세요"
                return@setOnClickListener
            }

            val sh = tpStart.hour
            val sm = tpStart.minute
            val eh = tpEnd.hour
            val em = tpEnd.minute

            val startMin = sh * 60 + sm
            val endMin = eh * 60 + em

            if (endMin <= startMin) {
                Toast.makeText(this, "종료 시간은 시작 시간보다 늦어야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val startTime = String.format("%02d:%02d", sh, sm)
            val endTime = String.format("%02d:%02d", eh, em)

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val entity = ScheduleEntity(
                        date = selectedDate,
                        title = title,
                        startTime = startTime,
                        endTime = endTime
                    )
                    scheduleDao.insertSchedule(entity)
                }

                dialog.dismiss()
                loadSchedules(selectedDate)
            }
        }

        dialog.show()
    }
}
