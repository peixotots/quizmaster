package com.example.quizandroid.ui.login

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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

    val userName =
        offlineCofre.getString("name_$uid", null) ?: userLocal?.name ?: userPrefs.getName()
        ?: "Jogador"
    val userAvatar = offlineCofre.getString("avatar_$uid", null) ?: userLocal?.avatar ?: "👤"

    var userScoreState by remember { mutableIntStateOf(0) }
    val userScoreRoom = userLocal?.totalScore ?: 0

    LaunchedEffect(uid, userName, userScoreRoom) {
        if (uid.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    dbLocal.userDao().insertUser(
                        UserEntity(
                            uid = uid,
                            name = userName,
                            email = userPrefs.getEmail() ?: "",
                            totalScore = userScoreRoom,
                            quizzesDone = 0,
                            avatar = userAvatar
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val completedQuizzes = quizRepository.getUserCompletedQuizzes(uid)
                val calculatedScore = completedQuizzes.sumOf { it.score }
                userScoreState = maxOf(userScoreRoom, calculatedScore)
            }
        }
    }

    var quizzesAbertos by remember { mutableStateOf<List<Quiz>>(emptyList()) }
    var quizzesConcluidos by remember { mutableStateOf<List<Pair<Quiz, Int>>>(emptyList()) }
    var meusRascunhos by remember { mutableStateOf<List<Quiz>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }
    var quizToDelete by remember { mutableStateOf<Quiz?>(null) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yy • HH:mm", Locale.getDefault()) }

    DisposableEffect(uid) {
        if (uid.isEmpty()) return@DisposableEffect onDispose {}

        val quizzesListener = dbRemote.collection("quizzes")
            .addSnapshotListener { quizSnapshot, _ ->
                dbRemote.collection("users").document(uid).collection("completed_quizzes")
                    .addSnapshotListener { completedSnapshot, _ ->
                        val completedMap = completedSnapshot?.documents?.associate {
                            it.id to (it.getLong("score")?.toInt() ?: 0)
                        } ?: emptyMap()
                        val allQuizzes = quizSnapshot?.toObjects(Quiz::class.java) ?: emptyList()
                        val publicQuizzes = allQuizzes.filter { it.status != "rascunho" }

                        publicQuizzes.forEach { quiz ->
                            dbRemote.collection("questions").whereEqualTo("quizId", quiz.id).get()
                        }

                        quizzesAbertos = publicQuizzes.filter { !completedMap.containsKey(it.id) }
                            .sortedByDescending { it.createdAt }
                        quizzesConcluidos = publicQuizzes.filter { completedMap.containsKey(it.id) }
                            .map { Pair(it, completedMap[it.id] ?: 0) }
                            .sortedByDescending { it.first.createdAt }
                        isLoading = false
                    }
            }

        val draftsListener = dbRemote.collection("quizzes").whereEqualTo("authorId", uid)
            .whereEqualTo("status", "rascunho")
            .addSnapshotListener { snapshot, _ ->
                val drafts =
                    snapshot?.toObjects(Quiz::class.java)?.sortedByDescending { it.createdAt }
                        ?: emptyList()
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

    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Scaffold(
        containerColor = Color(0xFFF8F9FE),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreateQuiz,
                containerColor = Laranja,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Novo Quiz", fontWeight = FontWeight.Bold) })
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val navColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    indicatorColor = Laranja,
                    unselectedIconColor = Color.Gray
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    selected = true,
                    onClick = {},
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
                    selected = false,
                    onClick = onNavigateToProfile,
                    colors = navColors
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            Surface(
                color = Laranja,
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(60.dp),
                            shape = CircleShape,
                            color = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (userAvatar == "👤") {
                                    Text(
                                        userName.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = Laranja,
                                        fontSize = 28.sp
                                    )
                                } else {
                                    Text(userAvatar, fontSize = 36.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Oi, $userName",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Stars,
                                null,
                                tint = Color.Yellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "$userScoreState",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                indicator = { tabPositions ->
                    if (pagerState.currentPage < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = Laranja
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                title,
                                color = if (pagerState.currentPage == index) Laranja else Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Laranja) }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val currentTabTitle = tabs.getOrNull(page) ?: "Abertos"

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                    ) {
                        when (currentTabTitle) {
                            "Abertos" -> {
                                items(quizzesAbertos) { quiz ->
                                    QuizItem(
                                        title = quiz.title,
                                        subtitle = "${quiz.questionCount} questões",
                                        label = "Por: ${quiz.authorName}",
                                        date = dateFormat.format(Date(quiz.createdAt)),
                                        trailingIcon = {
                                            Icon(
                                                Icons.Default.PlayArrow,
                                                contentDescription = "Jogar",
                                                tint = Laranja,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }) { onPlayQuiz(quiz.id, quiz.title) }
                                }
                            }

                            "Concluídos" -> {
                                items(quizzesConcluidos) { (quiz, score) ->
                                    val dataCriacao = dateFormat.format(Date(quiz.createdAt))
                                    val acertos = score / 10
                                    val erros = quiz.questionCount - acertos
                                    QuizItem(
                                        title = quiz.title,
                                        subtitle = "✅ Acertos: $acertos   |   ❌ Erros: $erros",
                                        label = "Sua nota: $score",
                                        date = dataCriacao,
                                        trailingIcon = {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = "Concluído",
                                                tint = Color(0xFF4CAF50),
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    ) {}
                                }
                            }

                            "Rascunhos" -> {
                                items(meusRascunhos) { quiz ->
                                    QuizItem(
                                        title = quiz.title,
                                        subtitle = "Não publicado",
                                        label = "Toque para Editar",
                                        date = dateFormat.format(Date(quiz.createdAt)),
                                        trailingIcon = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(onClick = {
                                                    quizToDelete = quiz
                                                }) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        "Deletar",
                                                        tint = Color.Red
                                                    )
                                                }; IconButton(onClick = {
                                                coroutineScope.launch {
                                                    quizRepository.releaseQuiz(
                                                        quiz.id
                                                    )
                                                }
                                            }) {
                                                Icon(
                                                    Icons.Default.RocketLaunch,
                                                    "Publicar",
                                                    tint = Laranja
                                                )
                                            }
                                            }
                                        }) { onEditDraft(quiz.id, quiz.title) }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (quizToDelete != null) {
            AlertDialog(
                onDismissRequest = { quizToDelete = null },
                containerColor = Color.White,
                title = {
                    Text(
                        "Excluir Rascunho?",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                text = { Text("Deseja apagar '${quizToDelete?.title}'?", color = Color.DarkGray) },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            quizRepository.deleteQuiz(quizToDelete!!.id); quizToDelete = null
                        }
                    }) { Text("Excluir", color = Color.Red, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { quizToDelete = null }) {
                        Text(
                            "Cancelar",
                            color = Color.Gray
                        )
                    }
                })
        }
    }
}

@Composable
fun QuizItem(
    title: String,
    subtitle: String,
    label: String,
    date: String,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(text = subtitle, color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        color = Laranja,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    trailingIcon?.invoke()
                    if (date.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = date,
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}