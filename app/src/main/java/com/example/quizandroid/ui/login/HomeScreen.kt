package com.example.quizandroid.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quizandroid.data.model.AppDatabase
import com.example.quizandroid.ui.theme.Laranja
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // 1. Instância do Banco de Dados Local
    val dbLocal = AppDatabase.getDatabase(context)
    val uid = auth.currentUser?.uid ?: ""

    // 2. Observa o Utilizador no Room em Tempo Real
    val userLocal by dbLocal.userDao().getUserById(uid).collectAsState(initial = null)

    // 3. Define o nome: Se o Room tiver dados, usa o nome. Se não, usa "Jogador".
    val userName = userLocal?.name ?: "Jogador"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Master", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Laranja),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            "Sair",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            Text(
                "Olá, $userName!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text("Escolha um tema para começar:", color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de Temas
            val themes = listOf(
                QuizTheme("Bíblico (Exodus 3)", Icons.Default.Book, Color(0xFFFF9800)),
                QuizTheme("Tecnologia", Icons.Default.Build, Color(0xFF009688)),
                QuizTheme("Geral", Icons.Default.Star, Laranja)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(themes) { theme ->
                    ThemeCard(theme)
                }
            }
        }
    }
}

@Composable
fun ThemeCard(theme: QuizTheme) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { /* Futura navegação para o Quiz */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = theme.color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = theme.color.copy(alpha = 0.2f),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(theme.icon, null, modifier = Modifier.padding(10.dp), tint = theme.color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                theme.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}

data class QuizTheme(val title: String, val icon: ImageVector, val color: Color)