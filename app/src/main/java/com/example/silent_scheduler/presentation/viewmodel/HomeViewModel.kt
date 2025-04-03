package com.example.silent_scheduler.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silent_scheduler.data.local.entity.SilentSchedule
import com.example.silent_scheduler.domain.usecase.GetScheduleUseCase
import com.example.silent_scheduler.domain.usecase.SaveScheduleUseCase
import com.example.silent_scheduler.domain.usecase.ScheduleSilentModeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getScheduleUseCase: GetScheduleUseCase,
    private val saveScheduleUseCase: SaveScheduleUseCase,
    private val scheduleSilentModeUseCase: ScheduleSilentModeUseCase
) : ViewModel() {

    private val _schedule = MutableStateFlow<SilentSchedule?>(null)
    val schedule = _schedule.asStateFlow()

    init {
        loadSchedule()
    }

    fun loadSchedule() {
        viewModelScope.launch {
            _schedule.value = getScheduleUseCase()
        }
    }

    fun saveSchedule(schedule: SilentSchedule, startMillis: Long, endMillis: Long, selectedDays: Set<String>) {
        viewModelScope.launch {
            saveScheduleUseCase(schedule)
            scheduleSilentModeUseCase.schedule(startMillis, endMillis, selectedDays)
            _schedule.value = schedule // Update the local state
        }
    }

    fun toggleSchedule(isEnabled: Boolean) {
        _schedule.value?.let {
            val updatedSchedule = it.copy(isEnabled = isEnabled)
            saveSchedule(updatedSchedule, 0L, 0L, emptySet()) // Persist change
        }
    }
}
