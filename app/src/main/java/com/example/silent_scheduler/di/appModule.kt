package com.example.silent_scheduler.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import androidx.room.Room
import com.example.silent_scheduler.data.local.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "silent_scheduler_db"
        ).build()
    }

    single { get<AppDatabase>().silentScheduleDao() }
    single {
        androidContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    single {
        androidContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    single {
        androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}
