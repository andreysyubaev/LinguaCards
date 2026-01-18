package com.example.linguacards.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.linguacards.data.model.Card
import com.example.linguacards.data.model.Pack

@Dao
interface PackDao {
    @Insert
    suspend fun insert(pack: Pack): Long

    @Update
    suspend fun update(pack: Pack)

    @Query("SELECT * FROM packs")
    suspend fun getAll(): List<Pack>

    @Delete
    suspend fun delete(pack: Pack)

    @Query("UPDATE packs SET name = :name WHERE id = :id")
    suspend fun updatePack(id: Int, name: String)

    @Query("SELECT COUNT(*) FROM packcard WHERE pack_id = :packId")
    suspend fun getCardsCount(packId: Int): Int

    @Query("SELECT * FROM packs WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Pack?

    @Query("SELECT * FROM packs WHERE name LIKE :query")
    suspend fun searchByName(query: String): List<Pack>

    @Query("UPDATE packs SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Int, favorite: Boolean)
}