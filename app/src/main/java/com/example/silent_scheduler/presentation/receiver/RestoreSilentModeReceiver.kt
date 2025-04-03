package com.example.silent_scheduler.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import com.example.silent_scheduler.data.local.dao.SilentScheduleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RestoreSilentModeReceiver : BroadcastReceiver(), KoinComponent {
    private val audioManager: AudioManager by inject()
    private val silentScheduleDao: SilentScheduleDao by inject()
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val action = intent.getStringExtra("action") ?: return
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val schedule = silentScheduleDao.getSchedule()
                if (schedule?.isEnabled != true) return@launch
                when (action) {
                    "activate" -> audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                    "deactivate" -> audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                }
            } catch (e: Exception) {
                Log.e("SilentMode", "Error in RestoreSilentModeReceiver", e)
            }
        }
    }
}