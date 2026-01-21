package com.example.timecatch

import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleManageActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var tvSelectedDate: TextView
    private lateinit var btnAddSchedule: Button

    private val cal = Calendar.getInstance()
    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_manage)

        calendarView = findViewById(R.id.calendarView)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        btnAddSchedule = findViewById(R.id.btnAddSchedule)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        // 초기 날짜
        tvSelectedDate.text = "${fmt.format(cal.time)} 일정"

        // 날짜 선택
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            cal.set(year, month, dayOfMonth)
            tvSelectedDate.text = "${fmt.format(cal.time)} 일정"
            // TODO: 선택 날짜 일정 로드
        }

        // + 일정 추가 클릭 -> 바텀시트 열기
        btnAddSchedule.setOnClickListener {
            openAddScheduleBottomSheet()
        }
    }

    private fun openAddScheduleBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_add_schedule, null)
        dialog.setContentView(view)

        view.findViewById<ImageButton>(R.id.btnClose).setOnClickListener { dialog.dismiss() }

        view.findViewById<Button>(R.id.btnAdd).setOnClickListener {
            // TODO: 입력값 읽어서 저장(Room DB)
            dialog.dismiss()
        }

        dialog.show()
    }
}
