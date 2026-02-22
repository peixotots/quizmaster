package com.example.quizandroid.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
  // corrija o import se necessário
import com.example.quizandroid.ui.quiz.Home_Screen
import com.example.quizandroid.ui.quizCategory.CategoryCarousel
import com.example.quizandroid.ui.quizCategory.CategoryCarouselScreen
import com.example.quizandroid.ui.quizCategory.ListScreen
import com.example.quizandroid.ui.quizCategory.GenericQuizScreen

@Composable
fun QuizNavHost(
    navController: NavHostController = rememberNavController(),
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            Home_Screen(
                onLogout = onLogout,
                onCategoryClick = { category ->
                    navController.navigate("carousel/$category") }
            )
        }
        composable("list/{category}",
            arguments = listOf(navArgument("category")
            {type = NavType.StringType

            }
        )
        ){ backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            ListScreen(
                onBack = { navController.popBackStack() },
                onOpenQuiz = { subtopic ->
                    navController.navigate("quiz/$category/$subtopic")
                }
            )
        }
        composable(
            "carousel/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            CategoryCarouselScreen(

                onCategoryClick = { category ->
                    navController.navigate("list/$category")
                }
            )
        }

        composable(
            route = "quiz/{category}/{subtopic}",
            arguments = listOf(
                navArgument("category")
                { type = NavType.StringType },
                navArgument("subtopic")
                {type = NavType.StringType}
            )

        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            val subtopic = backStackEntry.arguments?.getString("subtopic") ?: ""
            GenericQuizScreen(
                category = subtopic,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
