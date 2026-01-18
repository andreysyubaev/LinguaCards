package com.example.linguacards.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.linguacards.dao.CardDao
import com.example.linguacards.dao.PackCardDao
import com.example.linguacards.dao.PackDao
import com.example.linguacards.dao.UserDao

@Database(entities = [User::class, Card::class, Pack::class, PackCard::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cardDao(): CardDao
    abstract fun packDao(): PackDao
    abstract fun packCardDao(): PackCardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "cards_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}