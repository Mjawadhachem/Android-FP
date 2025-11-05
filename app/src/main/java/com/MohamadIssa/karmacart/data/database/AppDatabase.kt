package com.MohamadIssa.karmacart.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.MohamadIssa.karmacart.data.dao.PostDao
import com.MohamadIssa.karmacart.data.entity.Post

@Database(entities = [Post::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
 abstract fun postDao(): PostDao
}