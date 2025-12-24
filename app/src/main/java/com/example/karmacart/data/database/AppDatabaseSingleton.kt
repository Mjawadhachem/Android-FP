package com.example.karmacart.data.database

import android.content.Context
import androidx.room.Room

object AppDatabaseSingleton {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "karmacart.db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
