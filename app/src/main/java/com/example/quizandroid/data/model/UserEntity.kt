package com.example.quizandroid.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String,
    val totalScore: Int = 0,
    val quizzesDone: Int = 0,
    val avatar: String = "👤"
)