package com.example.karmacart.data.dao

import androidx.room.*
import com.example.karmacart.data.entity.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    // All posts (used for dashboard)
    @Query("SELECT * FROM Post ORDER BY id DESC")
    fun getAll(): Flow<List<Post>>

    // Only active (not completed) posts
    @Query("SELECT * FROM Post WHERE isCompleted = 0 ORDER BY id DESC")
    fun getActive(): Flow<List<Post>>

    // Insert new post
    @Insert
    suspend fun insert(post: Post)

    // Update entire post
    @Update
    suspend fun update(post: Post)

    // Mark post as completed
    @Query("UPDATE Post SET isCompleted = 1 WHERE id = :postId")
    suspend fun markAsCompleted(postId: Int)

    // Delete post
    @Delete
    suspend fun delete(post: Post)
}
