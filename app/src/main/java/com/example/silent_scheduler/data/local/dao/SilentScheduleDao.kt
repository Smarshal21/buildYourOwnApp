package com.example.silent_scheduler.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.silent_scheduler.data.local.entity.SilentSchedule

@Dao
interface SilentScheduleDao {
    @Insert
    suspend fun insert(schedule: SilentSchedule): Long

    @Query("SELECT * FROM silent_schedules LIMIT 1")
    suspend fun getSchedule(): SilentSchedule?

    @Query("DELETE FROM silent_schedules")
    suspend fun deleteAll()
}