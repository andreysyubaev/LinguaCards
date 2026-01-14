package com.example.linguacards.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.linguacards.data.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User) // добавление пользователя

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>
}