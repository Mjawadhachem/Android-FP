package com.example.karmacart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.karmacart.data.entity.Post
import com.example.karmacart.data.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(private val repo: PostRepository) : ViewModel() {

    // Current list (all posts)
    val posts = repo.posts.asLiveData()

    // Optional: only active posts (if later you want a toggle "Active only")
    val activePosts = repo.activePosts.asLiveData()

    fun addPost(
        type: String,
        title: String,
        desc: String,
        cat: String,
        contact: String
    ) {
        viewModelScope.launch {
            repo.add(
                Post(
                    type = type,
                    title = title,
                    description = desc,
                    category = cat,
                    contact = contact,
                    isCompleted = false
                )
            )
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            repo.remove(post)
        }
    }

    // NEW: mark a post as completed
    fun markPostCompleted(postId: Int) {
        viewModelScope.launch {
            repo.markAsCompleted(postId)
        }
    }
}
