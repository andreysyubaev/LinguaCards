package com.example.linguacards.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Entity(
    tableName = "packcard",
    foreignKeys = [
        ForeignKey(
            entity = Pack::class,
            parentColumns = ["id"],
            childColumns = ["pack_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["card_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PackCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pack_id: Int,
    val card_id: Int,
    val createdAt: Date = Date(),
    val lastReview: Date? = null
)

