package com.example.linguacards.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "packs")
data class Pack(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val cardsCount: Int,
    val createdAt: Date
)
