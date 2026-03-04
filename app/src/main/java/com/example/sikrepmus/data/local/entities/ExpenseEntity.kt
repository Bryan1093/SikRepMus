package com.example.sikrepmus.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val merchant: String,
    val date: Long, // Timestamp
    val category: String,
    val description: String = "",
    val imagePath: String? = null
)
