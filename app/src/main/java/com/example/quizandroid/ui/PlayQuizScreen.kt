package com.example.quizandroid.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizandroid.data.model.Question
import com.example.quizandroid.data.remote.QuizRepository
import com.example.quizandroid.ui.theme.Laranja
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayQuizScreen(
    quizId: String,
    quizTitle: String,
    onBack: () -> Unit,
    onQuizFinished: (Int) -> Unit
) {
    val repository = remember { QuizRepository() }
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var selectedAnswers by remember { mutableStateOf(mapOf<Int, Int>()) }
    var score by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(quizId) {
        questions = repository.getQuestionsByQuizId(quizId)
        isLoading = false
    }

    val backgroundColor = Color(0xFFF8F9FE)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text(quizTitle, fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Laranja)
            }
        } else if (questions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhuma pergunta encontrada para este quiz.")
            }
        } else {
            val pagerState = rememberPagerState(pageCount = { questions.size })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pergunta ${pagerState.currentPage + 1}/${questions.size}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { (pagerState.currentPage + 1).toFloat() / questions.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Laranja,
                    trackColor = Color.LightGray,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(32.dp))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    userScrollEnabled = false
                ) { page ->
                    val question = questions[page]
                    val hasAnswered = selectedAnswers.containsKey(page)
                    val selectedOption = selectedAnswers[page]

                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = question.text,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black,
                            lineHeight = 32.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        question.options.forEachIndexed { index, optionText ->
                            val isCorrectAnswer = index == question.correctAnswerIndex
                            val isSelected = index == selectedOption

                            val cardColor = when {
                                !hasAnswered -> Color.White
                                isCorrectAnswer -> Color(0xFF4CAF50)
                                isSelected && !isCorrectAnswer -> Color(0xFFF44336)
                                else -> Color.White
                            }

                            val textColor =
                                if (hasAnswered && (isCorrectAnswer || isSelected)) Color.White else Color.Black

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .height(64.dp)
                                    .clickable(enabled = !hasAnswered) {
                                        val newAnswers = selectedAnswers.toMutableMap()
                                        newAnswers[page] = index
                                        selectedAnswers = newAnswers

                                        if (isCorrectAnswer) score += 10

                                        coroutineScope.launch {
                                            kotlinx.coroutines.delay(1000)
                                            if (page < questions.size - 1) {
                                                pagerState.animateScrollToPage(page + 1)
                                            } else {
                                                onQuizFinished(score)
                                            }
                                        }
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = cardColor),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 20.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = optionText,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textColor
                                    )

                                    if (hasAnswered) {
                                        if (isCorrectAnswer) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                        } else if (isSelected) {
                                            Icon(
                                                Icons.Default.Cancel,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}