package com.example.silent_scheduler

import android.app.Application
import com.example.silent_scheduler.di.appModule
import com.example.silent_scheduler.di.repositoryModule
import com.example.silent_scheduler.di.useCaseModule
import com.example.silent_scheduler.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SilentSchedulerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SilentSchedulerApp)
            modules(
                appModule,
                useCaseModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}
