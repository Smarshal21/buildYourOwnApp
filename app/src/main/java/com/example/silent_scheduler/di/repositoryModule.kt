package com.example.silent_scheduler.di

import com.example.silent_scheduler.domain.repository.SilentScheduleRepository
import com.example.silent_scheduler.data.repository.SilentScheduleRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<SilentScheduleRepository> { SilentScheduleRepositoryImpl(get()) }
}
