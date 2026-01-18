package com.example.linguacards.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(
    tableName = "packs",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Pack(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val user_id: Int,
    val name: String,
    val createdAt: Date = Date(),
    val isFavorite: Boolean = false
) : Parcelable
