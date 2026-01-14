package com.example.linguacards.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.linguacards.data.model.Card

@Dao
interface CardDao {

    @Insert
    suspend fun insert(card: Card)

    @Query("SELECT * FROM cards")
    suspend fun getAll(): List<Card>

    @Delete
    suspend fun delete(card: Card)
}