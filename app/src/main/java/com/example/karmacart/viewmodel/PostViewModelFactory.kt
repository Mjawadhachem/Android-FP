package com.example.karmacart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.karmacart.data.repository.PostRepository

class PostViewModelFactory(private val repo: PostRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
