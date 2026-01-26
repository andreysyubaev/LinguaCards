package com.example.linguacards.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.linguacards.data.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): User?

    @Query("UPDATE users SET password = :newPassword WHERE id = :userId")
    suspend fun updatePassword(userId: Int, newPassword: String)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Int): User?

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Update
    suspend fun update(user: User)
}