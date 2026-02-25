package com.example.quizandroid.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

    val offlineCofre = context.getSharedPreferences("BypassOffline", Context.MODE_PRIVATE)
    val uid = auth.currentUser?.uid ?: offlineCofre.getString("uid", "") ?: ""
    val coroutineScope = rememberCoroutineScope()

    val userLocal by dbLocal.userDao().getUserById(uid).collectAsState(initial = null)

    var quizzesDoneState by remember { mutableIntStateOf(0) }
    var userScoreState by remember { mutableIntStateOf(0) }
    var totalCorrectAnswers by remember { mutableIntStateOf(0) }
    var totalWrongAnswers by remember { mutableIntStateOf(0) }

    var isBiometricEnabled by remember {
        mutableStateOf(
            offlineCofre.getBoolean(
                "biometric_enabled",
                false
            )
        )
    }

    val userName =
        offlineCofre.getString("name_$uid", null) ?: userLocal?.name ?: userPrefs.getName()
        ?: "Jogador"
    val userEmail =
        userLocal?.email ?: auth.currentUser?.email ?: offlineCofre.getString("email", "") ?: ""
    val userAvatar = offlineCofre.getString("avatar_$uid", null) ?: userLocal?.avatar ?: "👤"

    val quizzesDoneRoom = userLocal?.quizzesDone ?: 0
    val userScoreRoom = userLocal?.totalScore ?: 0

    var showAvatarDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var nameToEdit by remember { mutableStateOf(userName) }

    val availableAvatars = listOf(
        "👨‍🚀",
        "🦸‍♂️",
        "🥷",
        "🧙‍♂️",
        "🕵️‍♂️",
        "👩‍🚀",
        "🦸‍♀️",
        "🧚‍♀️",
        "🧝‍♀️",
        "🕵️‍♀️",
        "🐼",
        "🦊"
    )

    LaunchedEffect(uid, quizzesDoneRoom, userScoreRoom) {
        if (uid.isNotEmpty()) {
            val allQuizzesMap = quizRepo.getActiveQuizzes().associateBy { it.id }
            val completedQuizzes = quizRepo.getUserCompletedQuizzes(uid)

            val currentTotalScore = completedQuizzes.sumOf { it.score }
            val currentQuizzesDone = completedQuizzes.size
            val calculatedCorrect = currentTotalScore / 10

            var totalQuestionsFaced = 0
            completedQuizzes.forEach { attempt ->
                val quizDefinition = allQuizzesMap[attempt.quizId]
                totalQuestionsFaced += quizDefinition?.questionCount ?: 0
            }

            val calculatedWrong = totalQuestionsFaced - calculatedCorrect

            userScoreState = maxOf(userScoreRoom, currentTotalScore)
            quizzesDoneState = maxOf(quizzesDoneRoom, currentQuizzesDone)
            totalCorrectAnswers = calculatedCorrect
            totalWrongAnswers = calculatedWrong
        }
    }

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
                                offlineCofre.edit().putString("name_$uid", nameToEdit).apply()
                                try {
                                    userLocal?.let {
                                        dbLocal.userDao().insertUser(it.copy(name = nameToEdit))
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                try {
                                    FirebaseFirestore.getInstance().collection("users")
                                        .document(uid).update("name", nameToEdit)
                                } catch (e: Exception) {
                                }

                                userPrefs.saveUser(uid, nameToEdit, userEmail)
                                showEditNameDialog = false
                                Toast.makeText(context, "Nome atualizado!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Laranja)
                ) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text(
                        "Cancelar",
                        color = Color.Gray
                    )
                }
            }
        )
    }

    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            title = { Text("Escolha seu Avatar", fontWeight = FontWeight.Bold) },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(availableAvatars) { avatar ->
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(
                                    if (userAvatar == avatar) Laranja.copy(alpha = 0.3f) else Color(
                                        0xFFF0F0F0
                                    )
                                )
                                .clickable {
                                    coroutineScope.launch {
                                        offlineCofre.edit().putString("avatar_$uid", avatar).apply()
                                        try {
                                            userLocal?.let {
                                                dbLocal.userDao()
                                                    .insertUser(it.copy(avatar = avatar))
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        try {
                                            FirebaseFirestore.getInstance().collection("users")
                                                .document(uid).update("avatar", avatar)
                                        } catch (e: Exception) {
                                        }

                                        showAvatarDialog = false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) { Text(text = avatar, fontSize = 32.sp) }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarDialog = false }) {
                    Text(
                        "Cancelar",
                        color = Color.Gray
                    )
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FE),
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val navColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    indicatorColor = Laranja,
                    unselectedIconColor = Color.Gray
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    selected = false,
                    onClick = onNavigateToHome,
                    colors = navColors
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.EmojiEvents, null) },
                    selected = false,
                    onClick = onNavigateToRanking,
                    colors = navColors
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    selected = true,
                    onClick = { },
                    colors = navColors
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0F0F0))
                        .border(2.dp, Laranja.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (userAvatar == "👤") Text(
                        userName.take(1).uppercase(),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    else Text(userAvatar, fontSize = 56.sp)
                }
                Surface(
                    shape = CircleShape,
                    color = Laranja,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { showAvatarDialog = true },
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
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
                    modifier = Modifier
                        .size(18.dp)
                        .clickable {
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
                    Text("Meu Desempenho Geral", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val totalQuestions = totalCorrectAnswers + totalWrongAnswers
                        PerformanceDonutChart(
                            done = totalCorrectAnswers.toFloat(),
                            total = if (totalQuestions > 0) totalQuestions.toFloat() else 1f,
                            modifier = Modifier.size(110.dp)
                        )

                        Spacer(modifier = Modifier.width(24.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            LegendItem(
                                Color(0xFF4CAF50),
                                "Acertos Totais",
                                totalCorrectAnswers.toString()
                            )
                            LegendItem(
                                Color(0xFFF44336),
                                "Erros Totais",
                                totalWrongAnswers.toString()
                            )
                            LegendItem(Color.Gray, "Quizzes Feitos", quizzesDoneState.toString())
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Stars,
                    iconTint = Color(0xFFFFD700),
                    value = "$userScoreState",
                    label = "Pontos"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AssignmentTurnedIn,
                    iconTint = Laranja,
                    value = "$quizzesDoneState",
                    label = "Quizzes"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = Laranja,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Login por Biometria",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text("Entrar sem digitar a senha", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = { checked ->
                            isBiometricEnabled = checked
                            offlineCofre.edit().putBoolean("biometric_enabled", checked).apply()
                            Toast.makeText(
                                context,
                                if (checked) "Biometria Ativada" else "Biometria Desativada",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Laranja
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFF0F0),
                    contentColor = Color.Red
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sair da Conta", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    value: String,
    label: String
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color.Black
            )
            Text(text = label, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun PerformanceDonutChart(done: Float, total: Float, modifier: Modifier) {
    val rawProgress = if (total > 0) done / total else 0f
    val safeProgress = rawProgress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = safeProgress,
        animationSpec = tween(1000),
        label = ""
    )

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
            Text("Acertos", fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.width(90.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}