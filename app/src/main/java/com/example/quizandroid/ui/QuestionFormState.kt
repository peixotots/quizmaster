package com.example.quizandroid.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class QuestionFormState(alternativesCount: Int = 4) {
    var questionText by mutableStateOf("")
    // Cria a lista com o tamanho exato que você pediu
    var options by mutableStateOf(List(alternativesCount) { "" })
    var correctAnswerIndex by mutableStateOf(0)
}