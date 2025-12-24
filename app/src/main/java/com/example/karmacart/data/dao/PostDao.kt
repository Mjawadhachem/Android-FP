package com.example.karmacart.data.dao

import androidx.room.*
import com.example.karmacart.data.entity.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query("SELECT * FROM Post ORDER BY id DESC")
    fun getAll(): Flow<List<Post>>

    @Insert
    suspend fun insert(post: Post)

    @Update
    suspend fun update(post: Post)

    @Delete
    suspend fun delete(post: Post)
}
