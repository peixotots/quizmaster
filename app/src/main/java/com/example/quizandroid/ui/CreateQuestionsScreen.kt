package com.example.quizandroid.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quizandroid.ui.theme.Laranja

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuestionsScreen(
    questionCount: Int,
    alternativesCount: Int,
    quizTitle: String,
    initialQuestions: List<QuestionFormState>? = null,
    onBack: () -> Unit,
    onSave: (List<QuestionFormState>, String) -> Unit
) {
    val context = LocalContext.current

    val questionsStates = remember {
        mutableStateListOf<QuestionFormState>().apply {
            if (initialQuestions != null && initialQuestions.isNotEmpty()) {
                addAll(initialQuestions)
            } else {
                repeat(questionCount) { add(QuestionFormState(alternativesCount)) }
            }
        }
    }

    var showExitDialog by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedBorderColor = Laranja,
        unfocusedBorderColor = Color.LightGray,
        focusedLabelColor = Laranja,
        unfocusedLabelColor = Color.Gray,
        cursorColor = Laranja
    )

    BackHandler { showExitDialog = true }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = quizTitle,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(questionsStates.size) { index ->
                    QuestionCard(
                        index = index,
                        state = questionsStates[index],
                        fieldColors = fieldColors
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onSave(questionsStates.toList(), "rascunho") },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Text("RASCUNHO", fontWeight = FontWeight.Bold, color = Color.Gray)
                }

                Button(
                    onClick = {
                        val isValid =
                            questionsStates.all { it.questionText.isNotBlank() && it.options.all { opt -> opt.isNotBlank() } }
                        if (isValid) {
                            onSave(questionsStates.toList(), "ativo")
                        } else {
                            Toast.makeText(
                                context,
                                "Preencha tudo para publicar!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Laranja)
                ) {
                    Text("PUBLICAR", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Sair da Edição?", fontWeight = FontWeight.Bold) },
                text = { Text("As alterações não salvas serão perdidas.") },
                confirmButton = {
                    TextButton(onClick = onBack) {
                        Text(
                            "Sair",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showExitDialog = false
                    }) { Text("Continuar Editando", color = Laranja, fontWeight = FontWeight.Bold) }
                }
            )
        }
    }
}

@Composable
fun QuestionCard(index: Int, state: QuestionFormState, fieldColors: TextFieldColors) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Pergunta ${index + 1}", color = Laranja, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = state.questionText,
                onValueChange = { state.questionText = it },
                label = { Text("Enunciado") },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            state.options.forEachIndexed { optIndex, text ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = state.correctAnswerIndex == optIndex,
                        onClick = { state.correctAnswerIndex = optIndex },
                        colors = RadioButtonDefaults.colors(selectedColor = Laranja)
                    )
                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            val newList = state.options.toMutableList()
                            newList[optIndex] = it
                            state.options = newList
                        },
                        label = { Text("Opção ${optIndex + 1}") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
    }
}