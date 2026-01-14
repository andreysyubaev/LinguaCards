package com.example.linguacards.repositories

import android.content.Context
import com.example.linguacards.data.model.AppDataBase
import com.example.linguacards.data.model.Card
import java.util.Date

class CardRepository(context: Context) {
    private val db = AppDataBase.Companion.getDatabase(context)
    private val cardDao = db.cardDao()

    suspend fun addCard(term: String, definition: String, createdAt: Date, easeFactor: Float) {
        val card = Card(term = term, definition = definition, createdAt = createdAt, easeFactor = easeFactor)
        cardDao.insert(card)
    }
}