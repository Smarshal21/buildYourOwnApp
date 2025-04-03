package com.example.silent_scheduler.domain.usecase

import com.example.silent_scheduler.data.local.entity.SilentSchedule
import com.example.silent_scheduler.domain.repository.SilentScheduleRepository

class SaveScheduleUseCase(private val repository: SilentScheduleRepository) {
    suspend operator fun invoke(schedule: SilentSchedule) {
        repository.saveSchedule(schedule)
    }
}
