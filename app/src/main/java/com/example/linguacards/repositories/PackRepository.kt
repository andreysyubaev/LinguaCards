package com.example.linguacards.repositories

import android.content.Context
import com.example.linguacards.data.model.AppDataBase
import com.example.linguacards.data.model.Pack
import java.util.Date

class PackRepository (context: Context) {
    private val db = AppDataBase.Companion.getDatabase(context)
    private val packDao = db.packDao()

    suspend fun addPack(user_id: Int, name: String) {
        val pack = Pack(user_id = user_id, name = name)
        packDao.insert(pack)
    }
}