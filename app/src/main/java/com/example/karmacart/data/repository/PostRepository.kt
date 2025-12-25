package com.example.karmacart.data.repository

import com.example.karmacart.data.dao.PostDao
import com.example.karmacart.data.entity.Post
import kotlinx.coroutines.flow.Flow

class PostRepository(private val dao: PostDao) {

    // All posts (current dashboard)
    val posts: Flow<List<Post>> = dao.getAll()

    // Only active posts (optional filter feature)
    val activePosts: Flow<List<Post>> = dao.getActive()

    suspend fun add(post: Post) = dao.insert(post)

    suspend fun edit(post: Post) = dao.update(post)

    suspend fun remove(post: Post) = dao.delete(post)

    // NEW: mark as completed
    suspend fun markAsCompleted(postId: Int) = dao.markAsCompleted(postId)
}
