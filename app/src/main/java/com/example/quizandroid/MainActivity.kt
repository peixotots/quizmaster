package com.example.quizandroid

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.quizandroid.data.model.UserPrefsManager
import com.example.quizandroid.ui.login.HomeScreen
import com.example.quizandroid.ui.login.LoginScreen
import com.example.quizandroid.ui.login.RegisterScreen
import com.example.quizandroid.ui.CreateQuizSetupDialog
import com.example.quizandroid.ui.CreateQuestionsScreen
import com.example.quizandroid.ui.QuestionFormState // <-- IMPORTAÇÃO NECESSÁRIA
import com.example.quizandroid.ui.PlayQuizScreen
import com.example.quizandroid.data.remote.QuizRepository
import com.example.quizandroid.ui.theme.Laranja
import com.example.quizandroid.ui.theme.QuizAndroidTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPrefs = UserPrefsManager(this)

        setContent {
            QuizAndroidTheme {
                val context = LocalContext.current
                val coroutineScope = rememberCoroutineScope()
                val quizRepository = remember { QuizRepository() }

                var currentScreen by remember {
                    mutableStateOf(
                        if (userPrefs.isUserLoggedIn() || FirebaseAuth.getInstance().currentUser != null) "home" else "login"
                    )
                }

                var showSetupDialog by remember { mutableStateOf(false) }
                var setupTitle by remember { mutableStateOf("") }
                var setupCount by remember { mutableIntStateOf(5) }
                var setupAlternatives by remember { mutableIntStateOf(4) }

                // --- NOVO: Variáveis para guardar os dados da edição ---
                var editQuizId by remember { mutableStateOf<String?>(null) }
                var editInitialQuestions by remember { mutableStateOf<List<QuestionFormState>?>(null) }

                var playQuizId by remember { mutableStateOf("") }
                var playQuizTitle by remember { mutableStateOf("") }

                when (currentScreen) {
                    "login" -> LoginScreen(
                        onLoginSuccess = { currentScreen = "home" },
                        onNavigateToRegister = { currentScreen = "register" }
                    )

                    "register" -> RegisterScreen(
                        onRegisterSuccess = { currentScreen = "home" },
                        onNavigateToRegister = { currentScreen = "login" }
                    )

                    "home" -> {
                        HomeScreen(
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                userPrefs.clearUser()
                                currentScreen = "login"
                            },
                            onNavigateToCreateQuiz = { showSetupDialog = true },
                            onPlayQuiz = { id, title ->
                                playQuizId = id
                                playQuizTitle = title
                                currentScreen = "play_quiz"
                            },
                            // --- NOVO: Busca as perguntas antigas quando o usuário clica no rascunho ---
                            onEditDraft = { id, title ->
                                coroutineScope.launch {
                                    val questions = quizRepository.getQuestionsByQuizId(id)
                                    val loadedStates = questions.map { q ->
                                        QuestionFormState(q.options.size).apply {
                                            questionText = q.text
                                            options = q.options
                                            correctAnswerIndex = q.correctAnswerIndex
                                        }
                                    }
                                    setupTitle = title
                                    setupCount = loadedStates.size
                                    setupAlternatives = if (loadedStates.isNotEmpty()) loadedStates[0].options.size else 4
                                    editQuizId = id
                                    editInitialQuestions = loadedStates
                                    currentScreen = "create_questions"
                                }
                            },
                            onNavigateToRanking = { currentScreen = "ranking" },
                            onNavigateToProfile = { currentScreen = "profile" }
                        )

                        if (showSetupDialog) {
                            CreateQuizSetupDialog(
                                onDismiss = { showSetupDialog = false },
                                onNext = { count, title, alternatives ->
                                    setupCount = count
                                    setupTitle = title
                                    setupAlternatives = alternatives
                                    editQuizId = null // Garante que é um quiz novo
                                    editInitialQuestions = null // Garante que a tela vem vazia
                                    showSetupDialog = false
                                    currentScreen = "create_questions"
                                }
                            )
                        }
                    }

                    "ranking" -> {
                        com.example.quizandroid.ui.RankingScreen(
                            onNavigateToHome = { currentScreen = "home" },
                            onNavigateToProfile = { currentScreen = "profile" }
                        )
                    }

                    "profile" -> {
                        com.example.quizandroid.ui.ProfileScreen(
                            onNavigateToHome = { currentScreen = "home" },
                            onNavigateToRanking = { currentScreen = "ranking" },
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                userPrefs.clearUser()
                                currentScreen = "login"
                            }
                        )
                    }

                    "create_questions" -> {
                        var isPublishing by remember { mutableStateOf(false) }

                        if (isPublishing) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Laranja)
                            }
                        } else {
                            CreateQuestionsScreen(
                                questionCount = setupCount,
                                alternativesCount = setupAlternatives,
                                quizTitle = setupTitle,
                                initialQuestions = editInitialQuestions, // <-- Passa as perguntas preenchidas
                                onBack = {
                                    editQuizId = null
                                    editInitialQuestions = null
                                    currentScreen = "home"
                                },
                                onSave = { questionsList, finalStatus ->
                                    isPublishing = true
                                    val currentUser = FirebaseAuth.getInstance().currentUser
                                    val authorId = currentUser?.uid ?: ""
                                    val authorName = userPrefs.getName() ?: "Membro"

                                    coroutineScope.launch {
                                        val success = quizRepository.publishQuiz(
                                            quizIdToUpdate = editQuizId, // <-- Avisa se é atualização ou novo
                                            title = setupTitle,
                                            authorId = authorId,
                                            authorName = authorName,
                                            questionsStates = questionsList,
                                            status = finalStatus
                                        )

                                        isPublishing = false
                                        if (success) {
                                            editQuizId = null
                                            editInitialQuestions = null
                                            val msg = if(finalStatus == "rascunho") "Rascunho atualizado!" else "Publicado!"
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            currentScreen = "home"
                                        } else {
                                            Toast.makeText(context, "Erro ao salvar.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                        }
                    }

                    "play_quiz" -> {
                        val dbLocal = remember { com.example.quizandroid.data.model.AppDatabase.getDatabase(context) }
                        val dbRemote = remember { com.google.firebase.firestore.FirebaseFirestore.getInstance() }
                        val currentUser = FirebaseAuth.getInstance().currentUser

                        PlayQuizScreen(
                            quizId = playQuizId,
                            quizTitle = playQuizTitle,
                            onBack = { currentScreen = "home" },
                            onQuizFinished = { finalScore ->
                                if (currentUser != null) {
                                    coroutineScope.launch {
                                        try {
                                            quizRepository.saveQuizAttempt(currentUser.uid, playQuizId, finalScore)
                                            dbLocal.userDao().updateScore(currentUser.uid, finalScore)
                                            dbRemote.collection("users").document(currentUser.uid)
                                                .update(
                                                    "score", com.google.firebase.firestore.FieldValue.increment(finalScore.toLong()),
                                                    "quizzesDone", com.google.firebase.firestore.FieldValue.increment(1)
                                                )
                                            Toast.makeText(context, "Parabéns! $finalScore pontos.", Toast.LENGTH_LONG).show()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        currentScreen = "home"
                                    }
                                } else {
                                    currentScreen = "home"
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}