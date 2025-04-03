package com.example.silent_scheduler.data.repository

import com.example.silent_scheduler.data.local.dao.SilentScheduleDao
import com.example.silent_scheduler.data.local.entity.SilentSchedule
import com.example.silent_scheduler.domain.repository.SilentScheduleRepository

class SilentScheduleRepositoryImpl(private val dao: SilentScheduleDao) : SilentScheduleRepository {
    override suspend fun getSchedule(): SilentSchedule? = dao.getSchedule()

    override suspend fun saveSchedule(schedule: SilentSchedule) {
        dao.deleteAll()
        dao.insert(schedule)
    }
    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
