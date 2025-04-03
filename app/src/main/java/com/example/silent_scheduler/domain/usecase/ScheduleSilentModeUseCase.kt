package com.example.silent_scheduler.domain.usecase

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import com.example.silent_scheduler.presentation.receiver.RestoreSilentModeReceiver
import java.util.Calendar

class ScheduleSilentModeUseCase(private val alarmManager: AlarmManager, private val context: Context) {

    fun schedule(startMillis: Long, endMillis: Long, selectedDays: Set<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Show a message and open settings to request permission
            Toast.makeText(context, "Please enable exact alarms in settings", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        }

        val dayMap = mapOf(
            Calendar.SUNDAY to "Sun",
            Calendar.MONDAY to "Mon",
            Calendar.TUESDAY to "Tue",
            Calendar.WEDNESDAY to "Wed",
            Calendar.THURSDAY to "Thu",
            Calendar.FRIDAY to "Fri",
            Calendar.SATURDAY to "Sat"
        )

        for ((calendarDay, dayString) in dayMap) {
            if (dayString in selectedDays) {
                setAlarm(alarmManager,startMillis, calendarDay * 10 + 1, "activate")
                setAlarm(alarmManager,endMillis, calendarDay * 10 + 2, "deactivate")
            }
        }
    }
        private fun setAlarm(
        alarmManager: AlarmManager,
        timeInMillis: Long, requestCode: Int,
        action: String,

    ) {
        val intent = Intent(context, RestoreSilentModeReceiver::class.java).apply {
            putExtra("action", action)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

}
