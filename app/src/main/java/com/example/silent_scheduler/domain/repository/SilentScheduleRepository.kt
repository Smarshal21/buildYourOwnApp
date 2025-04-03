package com.example.silent_scheduler.domain.repository

import com.example.silent_scheduler.data.local.entity.SilentSchedule

interface SilentScheduleRepository {
    suspend fun getSchedule(): SilentSchedule?
    suspend fun saveSchedule(schedule: SilentSchedule)
    suspend fun deleteAll()
}
