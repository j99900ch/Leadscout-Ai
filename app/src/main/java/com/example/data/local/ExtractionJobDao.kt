package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ExtractionJobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtractionJobDao {
    @Query("SELECT * FROM extraction_jobs ORDER BY timestamp DESC")
    fun getAllJobs(): Flow<List<ExtractionJobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: ExtractionJobEntity): Long

    @Delete
    suspend fun deleteJob(job: ExtractionJobEntity)

    @Query("DELETE FROM extraction_jobs")
    suspend fun clearAllJobs()
}
