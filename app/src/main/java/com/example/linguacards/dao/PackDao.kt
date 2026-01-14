package com.example.linguacards.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.linguacards.data.model.Pack

@Dao
interface PackDao {
    @Insert
    suspend fun insert(pack: Pack)

    @Query("SELECT * FROM packs")
    suspend fun getAll(): List<Pack>

    @Delete
    suspend fun delete(pack: Pack)
}