package com.example.silent_scheduler.domain.usecase

import com.example.silent_scheduler.data.local.entity.SilentSchedule
import com.example.silent_scheduler.domain.repository.SilentScheduleRepository

class GetScheduleUseCase(private val repository: SilentScheduleRepository) {
    suspend operator fun invoke(): SilentSchedule? {
        return repository.getSchedule()
    }
}
