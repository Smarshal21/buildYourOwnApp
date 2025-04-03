package com.example.silent_scheduler.di

import com.example.silent_scheduler.domain.usecase.GetScheduleUseCase
import com.example.silent_scheduler.domain.usecase.SaveScheduleUseCase
import com.example.silent_scheduler.domain.usecase.ScheduleSilentModeUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetScheduleUseCase(get()) }
    factory { SaveScheduleUseCase(get()) }
    factory { ScheduleSilentModeUseCase(get(), get()) }
}

