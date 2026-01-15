package com.example.linguacards.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val term: String,
    val definition: String,
    val createdAt: Date = Date(),
    val easeFactor: Float
) : Parcelable
