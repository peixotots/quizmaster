package com.example.quizandroid.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizandroid.data.model.AppDatabase
import com.example.quizandroid.data.model.UserPrefsManager
import com.example.quizandroid.data.remote.QuizRepository
import com.example.quizandroid.ui.theme.Laranja
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRanking: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val dbLocal = remember { AppDatabase.getDatabase(context) }
    val quizRepo = remember { QuizRepository() }
    val userPrefs = remember { UserPrefsManager(context) }

    // 1. Pega o ID do nosso Cofre Offline!
    val offlineCofre = context.getSharedPreferences("BypassOffline", Context.MODE_PRIVATE)
    val uid = auth.currentUser?.uid ?: offlineCofre.getString("uid", "") ?: ""
    val coroutineScope = rememberCoroutineScope()

    val userLocal by dbLocal.userDao().getUserById(uid).collectAsState(initial = null)

    // Variáveis Blindadas para o Modo Offline
    var totalQuizzesCount by remember { mutableIntStateOf(0) }
    var quizzesDoneState by remember { mutableIntStateOf(0) }
    var userScoreState by remember { mutableIntStateOf(0) }

    val userName = userLocal?.name ?: userPrefs.getName() ?: offlineCofre.getString("name", "Jogador") ?: "Jogador"
    val userEmail = userLocal?.email ?: auth.currentUser?.email ?: offlineCofre.getString("email", "") ?: ""
    val userAvatar = userLocal?.avatar ?: "👤"

    val quizzesDoneRoom = userLocal?.quizzesDone ?: 0
    val userScoreRoom = userLocal?.totalScore ?: 0

    var showAvatarDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var nameToEdit by remember { mutableStateOf(userName) }

    val availableAvatars = listOf("👨‍🚀", "🦸‍♂️", "🥷", "🧙‍♂️", "🕵️‍♂️", "👩‍🚀", "🦸‍♀️", "🧚‍♀️", "🧝‍♀️", "🕵️‍♀️", "🐼", "🦊")

    // 2. A MÁGICA DOS GRÁFICOS: Lê as suas respostas do Cache e soma tudo!
    LaunchedEffect(uid, quizzesDoneRoom, userScoreRoom) {
        if (uid.isNotEmpty()) {
            val allQuizzes = quizRepo.getActiveQuizzes()
            val completedQuizzes = quizRepo.getUserCompletedQuizzes(uid) // Puxa seus 3 respondidos offline

            val calculatedScore = completedQuizzes.sumOf { it.score }

            // Força o gráfico a usar o maior valor, garantindo que os offline apareçam
            userScoreState = maxOf(userScoreRoom, calculatedScore)
            quizzesDoneState = maxOf(quizzesDoneRoom, completedQuizzes.size)

            totalQuizzesCount = maxOf(allQuizzes.size, quizzesDoneState)
        }
    }

    // --- DIALOG PARA EDITAR NOME ---
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Editar Nome", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = nameToEdit,
                    onValueChange = { nameToEdit = it },
                    label = { Text("Seu Nome") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameToEdit.isNotBlank()) {
                            coroutineScope.launch {
                                try {
                                    FirebaseFirestore.getInstance().collection("users").document(uid).update("name", nameToEdit)
                                } catch (e: Exception) { /* Ignora se tiver offline */ }

                                try {
                                    userLocal?.let { dbLocal.userDao().insertUser(it.copy(name = nameToEdit)) }
                                } catch (e: Exception) { e.printStackTrace() }

                                userPrefs.saveUser(uid, nameToEdit, userEmail)
                                showEditNameDialog = false
                                Toast.makeText(context, "Nome atualizado!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Laranja)
                ) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }

    // --- DIALOG PARA ESCOLHA DE AVATAR ---
    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            title = { Text("Escolha seu Avatar", fontWeight = FontWeight.Bold) },
            text = {
                LazyVerticalGrid(columns = GridCells.Fixed(3), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(availableAvatars) { avatar ->
                        Box(
                            modifier = Modifier.size(60.dp).clip(CircleShape)
                                .background(if (userAvatar == avatar) Laranja.copy(alpha = 0.3f) else Color(0xFFF0F0F0))
                                .clickable {
                                    coroutineScope.launch {
                                        try { dbLocal.userDao().updateAvatar(uid, avatar) } catch (e: Exception) { }
                                        try { FirebaseFirestore.getInstance().collection("users").document(uid).update("avatar", avatar) } catch (e: Exception) { }
                                        showAvatarDialog = false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) { Text(text = avatar, fontSize = 32.sp) }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAvatarDialog = false }) { Text("Cancelar", color = Color.Gray) } }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FE),
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, selected = false, onClick = onNavigateToHome, colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray))
                NavigationBarItem(icon = { Icon(Icons.Default.EmojiEvents, null) }, selected = false, onClick = onNavigateToRanking, colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray))
                NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, selected = true, onClick = { }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Laranja))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFFF0F0F0))
                        .border(2.dp, Laranja.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (userAvatar == "👤") Text(userName.take(1).uppercase(), fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    else Text(userAvatar, fontSize = 56.sp)
                }
                Surface(shape = CircleShape, color = Laranja, modifier = Modifier.size(32.dp).clickable { showAvatarDialog = true }, shadowElevation = 2.dp) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.PhotoCamera, null, tint = Color.White, modifier = Modifier.size(16.dp)) }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(userName, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar Nome",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp).clickable {
                        nameToEdit = userName
                        showEditNameDialog = true
                    }
                )
            }

            Text(userEmail, color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Meu Desempenho", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PerformanceDonutChart(
                            done = quizzesDoneState.toFloat(),
                            total = if(totalQuizzesCount > 0) totalQuizzesCount.toFloat() else 1f,
                            modifier = Modifier.size(110.dp)
                        )

                        Spacer(modifier = Modifier.width(24.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            LegendItem(Color(0xFF4CAF50), "Concluídos", quizzesDoneState.toString())
                            val abertos = (totalQuizzesCount - quizzesDoneState).coerceAtLeast(0)
                            LegendItem(Laranja, "Abertos", abertos.toString())
                            LegendItem(Color(0xFFF44336), "Erros Média", (quizzesDoneState * 0.2).toInt().toString())
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(modifier = Modifier.weight(1f), icon = Icons.Default.Stars, iconTint = Color(0xFFFFD700), value = "$userScoreState", label = "Pontos")
                StatCard(modifier = Modifier.weight(1f), icon = Icons.Default.AssignmentTurnedIn, iconTint = Laranja, value = "$quizzesDoneState", label = "Quizzes")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF0F0), contentColor = Color.Red),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sair da Conta", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun StatCard(modifier: Modifier = Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, value: String, label: String) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.Black)
            Text(text = label, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun PerformanceDonutChart(done: Float, total: Float, modifier: Modifier) {
    val rawProgress = if (total > 0) done / total else 0f
    val safeProgress = rawProgress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = safeProgress, animationSpec = tween(1000), label = "")

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = Color(0xFFF0F0F0), style = Stroke(width = 25f))
            drawArc(
                color = Color(0xFF4CAF50),
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress,
                useCenter = false,
                style = Stroke(width = 25f, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(safeProgress * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("Foco", fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.width(80.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}