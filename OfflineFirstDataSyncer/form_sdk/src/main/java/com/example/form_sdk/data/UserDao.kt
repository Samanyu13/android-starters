package com.example.form_sdk.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(data: UserProfile)

    @Query("SELECT * FROM user_profile WHERE isSynced = 0")
    suspend fun getUnsyncedData(): List<UserProfile>

    @Query("UPDATE user_profile SET isSynced = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: Int, status: Boolean)

    @Query("SELECT * FROM user_profile ORDER BY id DESC")
    fun getAllData(): Flow<List<UserProfile>>
}