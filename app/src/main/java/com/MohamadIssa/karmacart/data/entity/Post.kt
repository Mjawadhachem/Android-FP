package com.MohamadIssa.karmacart.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Post (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
            val type: String,
            val title: String,
            val description: String,
            val category: String,
            val contact: String
)