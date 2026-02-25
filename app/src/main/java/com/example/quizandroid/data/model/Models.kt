package com.example.quizandroid.data.model

data class Quiz(
    val id: String = "",
    val title: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val questionCount: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "ativo"
)

data class Question(
    val id: String = "",
    val quizId: String = "",
    val text: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = 0,
    val orderIndex: Int = 0
)