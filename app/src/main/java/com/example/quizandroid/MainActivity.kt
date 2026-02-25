package com.example.quizandroid

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.example.quizandroid.data.model.AppDatabase
import com.example.quizandroid.data.model.UserPrefsManager
import com.example.quizandroid.data.remote.QuizRepository
import com.example.quizandroid.ui.CreateQuestionsScreen
import com.example.quizandroid.ui.CreateQuizSetupDialog
import com.example.quizandroid.ui.PlayQuizScreen
import com.example.quizandroid.ui.QuestionFormState
import com.example.quizandroid.ui.login.HomeScreen
import com.example.quizandroid.ui.login.LoginScreen
import com.example.quizandroid.ui.login.RegisterScreen
import com.example.quizandroid.ui.theme.Laranja
import com.example.quizandroid.ui.theme.QuizAndroidTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPrefs = UserPrefsManager(this)

        setContent {
            QuizAndroidTheme {
                val context = LocalContext.current
                val coroutineScope = rememberCoroutineScope()
                val quizRepository = remember { QuizRepository() }

                val dbLocal = remember { AppDatabase.getDatabase(context) }
                val offlineCofre =
                    context.getSharedPreferences("BypassOffline", Context.MODE_PRIVATE)

                val auth = FirebaseAuth.getInstance()
                var currentScreen by remember {
                    mutableStateOf(
                        if (userPrefs.isUserLoggedIn() || auth.currentUser != null) "home" else "login"
                    )
                }

                var showSetupDialog by remember { mutableStateOf(false) }
                var setupTitle by remember { mutableStateOf("") }
                var setupCount by remember { mutableIntStateOf(5) }
                var setupAlternatives by remember { mutableIntStateOf(4) }

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
                                auth.signOut()
                                userPrefs.clearUser()
                                currentScreen = "login"
                            },
                            onNavigateToCreateQuiz = { showSetupDialog = true },
                            onPlayQuiz = { id, title ->
                                playQuizId = id
                                playQuizTitle = title
                                currentScreen = "play_quiz"
                            },
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
                                    setupAlternatives =
                                        if (loadedStates.isNotEmpty()) loadedStates[0].options.size else 4
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
                                    editQuizId = null
                                    editInitialQuestions = null
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
                                auth.signOut()
                                userPrefs.clearUser()
                                currentScreen = "login"
                            }
                        )
                    }

                    "create_questions" -> {
                        var isPublishing by remember { mutableStateOf(false) }

                        if (isPublishing) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Laranja)
                            }
                        } else {
                            CreateQuestionsScreen(
                                questionCount = setupCount,
                                alternativesCount = setupAlternatives,
                                quizTitle = setupTitle,
                                initialQuestions = editInitialQuestions,
                                onBack = {
                                    editQuizId = null
                                    editInitialQuestions = null
                                    currentScreen = "home"
                                },
                                onSave = { questionsList, finalStatus ->
                                    isPublishing = true

                                    val authorId =
                                        auth.currentUser?.uid ?: offlineCofre.getString("uid", "")
                                        ?: ""
                                    val authorName = userPrefs.getName() ?: "Membro"

                                    coroutineScope.launch {
                                        val success = quizRepository.publishQuiz(
                                            quizIdToUpdate = editQuizId,
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
                                            val msg =
                                                if (finalStatus == "rascunho") "Rascunho atualizado!" else "Publicado!"
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            currentScreen = "home"
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Erro ao salvar.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            )
                        }
                    }

                    "play_quiz" -> {
                        val dbRemote =
                            remember { com.google.firebase.firestore.FirebaseFirestore.getInstance() }

                        PlayQuizScreen(
                            quizId = playQuizId,
                            quizTitle = playQuizTitle,
                            onBack = { currentScreen = "home" },
                            onQuizFinished = { finalScore ->

                                val uid =
                                    auth.currentUser?.uid ?: offlineCofre.getString("uid", "") ?: ""

                                if (uid.isNotEmpty()) {
                                    coroutineScope.launch {
                                        try {
                                            quizRepository.saveQuizAttempt(
                                                uid,
                                                playQuizId,
                                                finalScore
                                            )

                                            try {
                                                dbLocal.userDao().updateScore(uid, finalScore)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }

                                            try {
                                                dbRemote.collection("users").document(uid)
                                                    .update(
                                                        "score",
                                                        com.google.firebase.firestore.FieldValue.increment(
                                                            finalScore.toLong()
                                                        ),
                                                        "quizzesDone",
                                                        com.google.firebase.firestore.FieldValue.increment(
                                                            1
                                                        )
                                                    )
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }

                                            Toast.makeText(
                                                context,
                                                "Parabéns! $finalScore pontos.",
                                                Toast.LENGTH_LONG
                                            ).show()
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