package com.example.karmacart.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.karmacart.data.dao.PostDao
import com.example.karmacart.data.entity.Post

@Database(entities = [Post::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}
