package com.example.quizandroid.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class QuestionFormState(alternativesCount: Int = 4) {
    var questionText by mutableStateOf("")
    var options by mutableStateOf(List(alternativesCount) { "" })
    var correctAnswerIndex by mutableStateOf(0)
}