package com.example.linguacards.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.linguacards.data.model.PackCard
import java.util.Date

@Dao
interface PackCardDao {
    @Insert
    suspend fun insert(packCard: PackCard): Long

    @Query("SELECT * FROM packcard")
    suspend fun getAll(): List<PackCard>

    @Delete
    suspend fun delete(packCard: PackCard)

    @Query("UPDATE packcard SET lastReview = :lastReview WHERE id = :id")
    suspend fun updateLastReview(id: Int, lastReview: Date)

    @Query("SELECT COUNT(*) FROM packcard WHERE pack_id = :packId")
    suspend fun getCardsCount(packId: Int): Int

    @Query("SELECT card_id FROM packcard WHERE pack_id = :packId")
    suspend fun getCardIds(packId: Int): List<Int>

    @Query("DELETE FROM PackCard WHERE pack_id = :packId")
    suspend fun deleteByPackId(packId: Int)

    @Query("SELECT * FROM packcard WHERE pack_id = :packId")
    suspend fun getByPackId(packId: Int): List<PackCard>
}