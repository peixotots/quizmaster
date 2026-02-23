package com.example.quizandroid.ui.login

import android.content.Context
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
    val dbRemote = FirebaseFirestore.getInstance()

    val offlineCofre = context.getSharedPreferences("BypassOffline", Context.MODE_PRIVATE)
    val uid = auth.currentUser?.uid ?: offlineCofre.getString("uid", "") ?: ""

    val userLocal by dbLocal.userDao().getUserById(uid).collectAsState(initial = null)
    val userName = userLocal?.name ?: userPrefs.getName() ?: "Jogador"
    val userScore = userLocal?.totalScore ?: 0
    val userAvatar = userLocal?.avatar ?: "👤"

    LaunchedEffect(uid, userName, userScore) {
        if (uid.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    dbLocal.userDao().insertUser(
                        UserEntity(uid = uid, name = userName, email = userPrefs.getEmail() ?: "", totalScore = userScore, quizzesDone = 0, avatar = userAvatar)
                    )
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    var quizzesAbertos by remember { mutableStateOf<List<Quiz>>(emptyList()) }
    var quizzesConcluidos by remember { mutableStateOf<List<Pair<Quiz, Int>>>(emptyList()) }
    var meusRascunhos by remember { mutableStateOf<List<Quiz>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var quizToDelete by remember { mutableStateOf<Quiz?>(null) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yy • HH:mm", Locale.getDefault()) }

    DisposableEffect(uid) {
        if (uid.isEmpty()) return@DisposableEffect onDispose {}

        val quizzesListener = dbRemote.collection("quizzes")
            .addSnapshotListener { quizSnapshot, _ ->
                dbRemote.collection("users").document(uid).collection("completed_quizzes")
                    .addSnapshotListener { completedSnapshot, _ ->
                        val completedMap = completedSnapshot?.documents?.associate { it.id to (it.getLong("score")?.toInt() ?: 0) } ?: emptyMap()
                        val allQuizzes = quizSnapshot?.toObjects(Quiz::class.java) ?: emptyList()
                        val publicQuizzes = allQuizzes.filter { it.status != "rascunho" }

                        publicQuizzes.forEach { quiz ->
                            dbRemote.collection("questions").whereEqualTo("quizId", quiz.id).get()
                        }

                        quizzesAbertos = publicQuizzes.filter { !completedMap.containsKey(it.id) }.sortedByDescending { it.createdAt }
                        quizzesConcluidos = publicQuizzes.filter { completedMap.containsKey(it.id) }.map { Pair(it, completedMap[it.id] ?: 0) }.sortedByDescending { it.first.createdAt }
                        isLoading = false
                    }
            }

        val draftsListener = dbRemote.collection("quizzes").whereEqualTo("authorId", uid).whereEqualTo("status", "rascunho")
            .addSnapshotListener { snapshot, _ ->
                val drafts = snapshot?.toObjects(Quiz::class.java)?.sortedByDescending { it.createdAt } ?: emptyList()
                meusRascunhos = drafts

                drafts.forEach { draft ->
                    dbRemote.collection("questions").whereEqualTo("quizId", draft.id).get()
                }
            }

        onDispose {
            quizzesListener.remove()
            draftsListener.remove()
        }
    }

    val tabs = mutableListOf("Abertos", "Concluídos")
    if (meusRascunhos.isNotEmpty()) tabs.add("Rascunhos")
    if (selectedTabIndex >= tabs.size) { selectedTabIndex = tabs.size - 1 }

    Scaffold(
        containerColor = Color(0xFFF8F9FE),
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onNavigateToCreateQuiz, containerColor = Laranja, contentColor = Color.White, shape = RoundedCornerShape(16.dp), icon = { Icon(Icons.Default.Add, null) }, text = { Text("Novo Quiz", fontWeight = FontWeight.Bold) })
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
                    Surface(modifier = Modifier.size(45.dp), shape = CircleShape, color = Color(0xFFF0F0F0)) { Box(contentAlignment = Alignment.Center) { if (userAvatar == "👤") Text(userName.take(1).uppercase(), fontWeight = FontWeight.Bold) else Text(userAvatar, fontSize = 24.sp) } }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Oi, $userName", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }
                Surface(color = Color(0xFF2D2D44), shape = RoundedCornerShape(12.dp)) { Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Stars, null, tint = Color.Yellow, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(8.dp)); Text("$userScore", color = Color.White, fontWeight = FontWeight.Bold) } }
            }

            TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.Transparent, indicator = { tabPositions -> if (selectedTabIndex < tabPositions.size) { TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]), color = Laranja) } }) {
                tabs.forEachIndexed { index, title -> Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }, text = { Text(title, color = if (selectedTabIndex == index) Laranja else Color.Gray, fontWeight = FontWeight.Bold) }) }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Laranja) }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)) {
                    val currentTabTitle = tabs.getOrNull(selectedTabIndex) ?: "Abertos"
                    when (currentTabTitle) {
                        "Abertos" -> { items(quizzesAbertos) { quiz -> QuizItem(title = quiz.title, subtitle = "${quiz.questionCount} questões", label = "Por: ${quiz.authorName}", date = dateFormat.format(Date(quiz.createdAt)), colorStatus = Color.Green, trailingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = "Jogar", tint = Laranja, modifier = Modifier.size(32.dp)) }) { onPlayQuiz(quiz.id, quiz.title) } } }

                        // --- AQUI FOI FEITA A ALTERAÇÃO PARA MOSTRAR ACERTOS E ERROS ---
                        "Concluídos" -> {
                            items(quizzesConcluidos) { (quiz, score) ->
                                val dataCriacao = dateFormat.format(Date(quiz.createdAt))

                                // Calcula Acertos e Erros
                                val acertos = score / 10
                                val erros = quiz.questionCount - acertos

                                QuizItem(
                                    title = quiz.title,
                                    subtitle = "✅ Acertos: $acertos   |   ❌ Erros: $erros", // Atualizado aqui!
                                    label = "Sua nota: $score",
                                    date = dataCriacao,
                                    colorStatus = Color.Gray,
                                    trailingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = "Concluído", tint = Color(0xFF4CAF50), modifier = Modifier.size(28.dp)) }
                                ) {}
                            }
                        }
                        // -----------------------------------------------------------------

                        "Rascunhos" -> { items(meusRascunhos) { quiz -> QuizItem(title = quiz.title, subtitle = "Não publicado", label = "Toque para Editar", date = dateFormat.format(Date(quiz.createdAt)), colorStatus = Color.Yellow, trailingIcon = { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { quizToDelete = quiz }) { Icon(Icons.Default.Delete, "Deletar", tint = Color.Red) }; IconButton(onClick = { coroutineScope.launch { quizRepository.releaseQuiz(quiz.id) } }) { Icon(Icons.Default.RocketLaunch, "Publicar", tint = Laranja) } } }) { onEditDraft(quiz.id, quiz.title) } } }
                    }
                }
            }
        }
        if (quizToDelete != null) { AlertDialog(onDismissRequest = { quizToDelete = null }, containerColor = Color.White, title = { Text("Excluir Rascunho?", fontWeight = FontWeight.Bold, color = Color.Black) }, text = { Text("Deseja apagar '${quizToDelete?.title}'?", color = Color.DarkGray) }, confirmButton = { TextButton(onClick = { coroutineScope.launch { quizRepository.deleteQuiz(quizToDelete!!.id); quizToDelete = null } }) { Text("Excluir", color = Color.Red, fontWeight = FontWeight.Bold) } }, dismissButton = { TextButton(onClick = { quizToDelete = null }) { Text("Cancelar", color = Color.Gray) } }) }
    }
}

@Composable
fun QuizItem(title: String, subtitle: String, label: String, date: String, colorStatus: Color, trailingIcon: @Composable (() -> Unit)? = null, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).background(colorStatus, CircleShape)); Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) { Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp); Text(text = subtitle, color = Color.Gray, fontSize = 14.sp); Text(text = label, color = Laranja, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) { if (date.isNotEmpty()) { Text(text = date, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal); Spacer(modifier = Modifier.height(4.dp)) }; trailingIcon?.invoke() }
        }
    }
}