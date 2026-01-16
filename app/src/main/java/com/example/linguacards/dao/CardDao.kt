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

    @Query("UPDATE cards SET term = :term, definition = :definition, easeFactor = :easeFactor WHERE id = :id")
    suspend fun updateCard(id: Int, term: String, definition: String, easeFactor: Float)

    @Query("SELECT * FROM cards WHERE id IN (:ids)")
    suspend fun getCardsByIds(ids: List<Int>): List<Card>

    @Query("SELECT * FROM cards WHERE term LIKE :query")
    suspend fun searchByTerm(query: String): List<Card>
}