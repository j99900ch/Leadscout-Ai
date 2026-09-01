package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LeadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads ORDER BY timestamp DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE entityType = :type ORDER BY timestamp DESC")
    fun getLeadsByType(type: String): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedLeads(): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE name LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%' OR state LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchLeads(query: String): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE id = :id LIMIT 1")
    suspend fun getLeadById(id: Long): LeadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeads(leads: List<LeadEntity>): List<Long>

    @Update
    suspend fun updateLead(lead: LeadEntity)

    @Delete
    suspend fun deleteLead(lead: LeadEntity)

    @Query("DELETE FROM leads WHERE id = :id")
    suspend fun deleteLeadById(id: Long)

    @Query("DELETE FROM leads")
    suspend fun clearAllLeads()

    @Query("SELECT COUNT(*) FROM leads")
    fun getLeadCount(): Flow<Int>

    @Query("SELECT name FROM leads")
    suspend fun getAllLeadNames(): List<String>

    @Query("SELECT * FROM leads ORDER BY timestamp DESC")
    suspend fun getAllLeadsList(): List<LeadEntity>
}
