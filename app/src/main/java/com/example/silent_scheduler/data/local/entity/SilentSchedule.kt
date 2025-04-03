package com.example.silent_scheduler.data.local.entity

// SilentSchedule.kt
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "silent_schedules")
data class SilentSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val days: String, // Comma-separated days (e.g., "Mon,Tue,Wed")
    val isEnabled: Boolean = false
)