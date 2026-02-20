package com.example.quizandroid.ui.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizandroid.data.model.AppDatabase
import com.example.quizandroid.data.model.Quiz
import com.example.quizandroid.data.model.UserEntity
import com.example.quizandroid.data.model.UserPrefsManager
import com.example.quizandroid.data.remote.QuizRepository
import com.example.quizandroid.ui.theme.Laranja
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToCreateQuiz: () -> Unit,
    onPlayQuiz: (String, String) -> Unit,
    onEditDraft: (String, String) -> Unit,
    onNavigateToRanking: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val dbLocal = remember { AppDatabase.getDatabase(context) }
    val userPrefs = remember { UserPrefsManager(context) }
    val quizRepository = remember { QuizRepository() }
    val coroutineScope = rememberCoroutineScope()
    val uid = auth.currentUser?.uid ?: ""

    val userLocal by dbLocal.userDao().getUserById(uid).collectAsState(initial = null)
    val userName = userLocal?.name ?: userPrefs.getName() ?: "Jogador"
    val userScore = userLocal?.totalScore ?: 0
    val userAvatar = userLocal?.avatar ?: "👤"

    var quizzesAbertos by remember { mutableStateOf<List<Quiz>>(emptyList()) }
    var quizzesConcluidos by remember { mutableStateOf<List<Pair<Quiz, Int>>>(emptyList()) }
    var meusRascunhos by remember { mutableStateOf<List<Quiz>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // --- CORREÇÃO AQUI: Adicionado Hora e Minuto ---
    val dateFormat = remember { SimpleDateFormat("dd/MM/yy • HH:mm", Locale.getDefault()) }

    val refreshData = {
        coroutineScope.launch {
            isLoading = true
            try {
                val doc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
                if (doc.exists()) {
                    dbLocal.userDao().insertUser(UserEntity(
                        uid = uid,
                        name = doc.getString("name") ?: userName,
                        email = auth.currentUser?.email ?: "",
                        totalScore = doc.getLong("score")?.toInt() ?: 0,
                        quizzesDone = doc.getLong("quizzesDone")?.toInt() ?: 0,
                        avatar = doc.getString("avatar") ?: "👤"
                    ))
                }

                val allActive = quizRepository.getActiveQuizzes()
                val myAttempts = quizRepository.getUserCompletedQuizzes(uid)
                val myDrafts = quizRepository.getMyDrafts(uid)

                val completedMap = myAttempts.associateBy { it.quizId }

                quizzesAbertos = allActive.filter { !completedMap.containsKey(it.id) }.sortedByDescending { it.createdAt }
                quizzesConcluidos = allActive.filter { completedMap.containsKey(it.id) }.sortedByDescending { it.createdAt }
                    .map { quiz -> Pair(quiz, completedMap[quiz.id]?.score ?: 0) }
                meusRascunhos = myDrafts.sortedByDescending { it.createdAt }

            } catch (e: Exception) { e.printStackTrace() }
            finally { isLoading = false }
        }
    }

    LaunchedEffect(uid) { refreshData() }

    val tabs = mutableListOf("Abertos", "Concluídos")
    if (meusRascunhos.isNotEmpty()) tabs.add("Rascunhos")

    if (selectedTabIndex >= tabs.size) {
        selectedTabIndex = tabs.size - 1
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FE),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreateQuiz,
                containerColor = Laranja,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Novo Quiz", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, selected = true, onClick = {}, colors = NavigationBarItemDefaults.colors(selectedIconColor = Laranja))
                NavigationBarItem(icon = { Icon(Icons.Default.EmojiEvents, null) }, selected = false, onClick = onNavigateToRanking, colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray))
                NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, selected = false, onClick = onNavigateToProfile, colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(45.dp), shape = CircleShape, color = Color(0xFFF0F0F0)) {
                        Box(contentAlignment = Alignment.Center) {
                            if (userAvatar == "👤") Text(userName.take(1).uppercase(), fontWeight = FontWeight.Bold)
                            else Text(userAvatar, fontSize = 24.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Oi, $userName", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }
                Surface(color = Color(0xFF2D2D44), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Stars, null, tint = Color.Yellow, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("$userScore", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]), color = Laranja)
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, color = if (selectedTabIndex == index) Laranja else Color.Gray, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Laranja) }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                ) {
                    val currentTabTitle = tabs.getOrNull(selectedTabIndex) ?: "Abertos"

                    when (currentTabTitle) {
                        "Abertos" -> {
                            items(quizzesAbertos) { quiz ->
                                val dataCriacao = dateFormat.format(Date(quiz.createdAt))
                                QuizItem(
                                    title = quiz.title,
                                    subtitle = "${quiz.questionCount} questões",
                                    label = "Por: ${quiz.authorName}",
                                    date = dataCriacao,
                                    colorStatus = Color.Green,
                                    trailingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = "Jogar", tint = Laranja, modifier = Modifier.size(32.dp)) }
                                ) { onPlayQuiz(quiz.id, quiz.title) }
                            }
                        }
                        "Concluídos" -> {
                            items(quizzesConcluidos) { (quiz, score) ->
                                val dataCriacao = dateFormat.format(Date(quiz.createdAt))
                                QuizItem(
                                    title = quiz.title,
                                    subtitle = "Finalizado",
                                    label = "Sua nota: $score",
                                    date = dataCriacao,
                                    colorStatus = Color.Gray,
                                    trailingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = "Concluído", tint = Color(0xFF4CAF50), modifier = Modifier.size(28.dp)) }
                                ) {}
                            }
                        }
                        "Rascunhos" -> {
                            items(meusRascunhos) { quiz ->
                                val dataCriacao = dateFormat.format(Date(quiz.createdAt))
                                QuizItem(
                                    title = quiz.title,
                                    subtitle = "Não publicado (${quiz.questionCount} questões)",
                                    label = "Toque para Editar",
                                    date = dataCriacao,
                                    colorStatus = Color.Yellow,
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            coroutineScope.launch {
                                                val success = quizRepository.releaseQuiz(quiz.id)
                                                if(success) {
                                                    Toast.makeText(context, "Publicado para todos!", Toast.LENGTH_SHORT).show()
                                                    refreshData()
                                                }
                                            }
                                        }) { Icon(Icons.Default.RocketLaunch, "Publicar", tint = Laranja) }
                                    }
                                ) { onEditDraft(quiz.id, quiz.title) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizItem(
    title: String,
    subtitle: String,
    label: String,
    date: String,
    colorStatus: Color,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).background(colorStatus, CircleShape))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = subtitle, color = Color.Gray, fontSize = 14.sp)
                Text(text = label, color = Laranja, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (date.isNotEmpty()) {
                    // --- CORREÇÃO AQUI: Fonte um pouco maior e mais fina para a hora aparecer bem ---
                    Text(text = date, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal)
                    Spacer(modifier = Modifier.height(4.dp))
                }
                trailingIcon?.invoke()
            }
        }
    }
}