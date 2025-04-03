package com.example.silent_scheduler.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.silent_scheduler.data.local.dao.SilentScheduleDao
import com.example.silent_scheduler.data.local.entity.SilentSchedule

@Database(
    entities = [SilentSchedule::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun silentScheduleDao(): SilentScheduleDao
}