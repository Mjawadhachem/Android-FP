package com.MohamadIssa.karmacart.data.dao

import androidx.room.*
import com.MohamadIssa.karmacart.data.entity.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("Select * FROM Post ORDER BY id DESC")
    fun getAll(): Flow<List<Post>>

    @Insert suspend fun insert(post: Post)
    @Update suspend fun update(post: Post)
    @Delete suspend fun delete(post: Post)
}