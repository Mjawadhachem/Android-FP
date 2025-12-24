package com.example.karmacart.data.repository

import com.example.karmacart.data.dao.PostDao
import com.example.karmacart.data.entity.Post

class PostRepository(private val dao: PostDao) {

    val posts = dao.getAll()

    suspend fun add(post: Post) = dao.insert(post)

    suspend fun edit(post: Post) = dao.update(post)

    suspend fun remove(post: Post) = dao.delete(post)
}
