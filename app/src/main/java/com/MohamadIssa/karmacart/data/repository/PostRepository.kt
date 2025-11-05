package com.MohamadIssa.karmacart.data.repository

import com.MohamadIssa.karmacart.data.dao.PostDao
import com.MohamadIssa.karmacart.data.entity.Post


class PostRepositoryprivas(private val dao: PostDao) {
    val posts = dao.getAll()
    suspend fun add(post: Post)=dao.insert(post)
    suspend fun edit(post: Post)=dao.update(post)
    suspend fun remove(post: Post)=dao.delete(post)
}