package com.example.karmacart.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,        // "REQUEST" or "DONATION"
    val title: String,       // e.g. "Need O+ blood at hospital"
    val description: String, // details
    val category: String,    // "Blood", "Furniture", "Food", ...
    val contact: String      // phone / WhatsApp / email
)
