package com.example.quizandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.quizandroid.data.model.UserPrefsManager
import com.example.quizandroid.ui.login.HomeScreen
import com.example.quizandroid.ui.login.LoginScreen
import com.example.quizandroid.ui.login.RegisterScreen
import com.example.quizandroid.ui.quiz.Home_Screen
import com.example.quizandroid.ui.quizCategory.CategoryCarousel
import com.example.quizandroid.ui.quizCategory.CategoryCarouselScreen
import com.example.quizandroid.ui.quizCategory.GenericQuizContent
import com.example.quizandroid.ui.quizCategory.GenericQuizScreen
import com.example.quizandroid.ui.quizCategory.ListScreen
import com.example.quizandroid.ui.theme.QuizAndroidTheme
import com.google.firebase.auth.FirebaseAuth



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPrefs = UserPrefsManager(this)

        setContent {
            QuizAndroidTheme {
                var currentScreen by remember { mutableStateOf(
                    if (userPrefs.isUserLoggedIn() || FirebaseAuth.getInstance().currentUser != null) "home" else "login"
                ) }

                when (currentScreen) {
                    "login" -> LoginScreen(
                        onLoginSuccess = { currentScreen = "home" },
                        onNavigateToRegister = { currentScreen = "register" }
                    )
                    "register" -> RegisterScreen(
                        onRegisterSuccess = { currentScreen = "home" },
                        onNavigateToRegister = { currentScreen = "login" }
                    )
//                    "home" -> HomeScreen(
//                        onLogout = {
//                            FirebaseAuth.getInstance().signOut()
//                            userPrefs.clearUser()
//                            currentScreen = "login"
//                        }
//                    )
                    "home" -> Home_Screen(
                       onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            userPrefs.clearUser()
                            currentScreen = "login"
                        } ,
                        onCategoryClick = { currentScreen = "categories" }
                    )

                    "carousel"-> CategoryCarouselScreen(
                        onCategoryClick = {currentScreen = "categories" }
                    )

                    "categories" -> ListScreen(
                        onBack = { currentScreen = "home" },
                        onOpenQuiz = { /* trate quiz */
                            currentScreen = "questao"
                        }
                    )

                    "questao" -> GenericQuizScreen(
                        category = "",
                        onBack = { currentScreen = "categories" }
                    )

                }
            }
        }
    }
}