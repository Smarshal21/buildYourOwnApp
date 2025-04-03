package com.example.silent_scheduler.di

import com.example.silent_scheduler.presentation.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        HomeViewModel(
            getScheduleUseCase = get(),
            saveScheduleUseCase = get(),
            scheduleSilentModeUseCase = get()
        )
    }
}
