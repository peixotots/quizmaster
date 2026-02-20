package com.example.quizandroid.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.quizandroid.ui.theme.Laranja

@Composable
fun CreateQuizSetupDialog(
    onDismiss: () -> Unit,
    onNext: (count: Int, title: String, alternatives: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var count by remember { mutableStateOf("5") }
    var alternatives by remember { mutableStateOf("4") }

    // Cores padronizadas para os campos de texto
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedBorderColor = Laranja,
        unfocusedBorderColor = Color.LightGray,
        focusedLabelColor = Laranja,
        unfocusedLabelColor = Color.Gray,
        cursorColor = Laranja
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White, // <-- Força o fundo do diálogo a ser branco
        titleContentColor = Color.Black, // <-- Força o título a ser preto
        textContentColor = Color.Black,
        title = { Text("Configurar Novo Quiz", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título do Quiz") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true, // <-- RESOLVE O PROBLEMA DO ENTER
                    colors = fieldColors
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = count,
                        onValueChange = { if (it.all { c -> c.isDigit() }) count = it },
                        label = { Text("Questões") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true, // <-- Impede pular linha
                        colors = fieldColors
                    )
                    OutlinedTextField(
                        value = alternatives,
                        onValueChange = { if (it.all { c -> c.isDigit() }) alternatives = it },
                        label = { Text("Opções") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true, // <-- Impede pular linha
                        colors = fieldColors
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onNext(count.toIntOrNull() ?: 5, title, alternatives.toIntOrNull() ?: 4)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Laranja)
            ) { Text("Próximo", color = Color.White, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray, fontWeight = FontWeight.Bold) }
        }
    )
}