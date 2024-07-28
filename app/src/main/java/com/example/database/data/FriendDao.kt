package com.example.database.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Insert
    suspend fun insert (friend: Friend)

    @Query ("SELECT * FROM Friend")
    fun getAll(): Flow<List<Friend>>

    @Update
    suspend fun update (friend: Friend)

    @Delete
    suspend fun delete (friend: Friend)
}