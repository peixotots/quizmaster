package com.example.quizandroid.ui.quiz

import android.media.Image
import android.provider.CalendarContract
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizandroid.R
import com.example.quizandroid.data.model.AppDatabase
import com.example.quizandroid.ui.quizCategory.CardCarrossel
import com.example.quizandroid.ui.quizCategory.CategoryCarousel
import com.example.quizandroid.ui.quizCategory.TitleCard
import com.example.quizandroid.ui.theme.Laranja
import com.example.quizandroid.ui.theme.LaranjaLight
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home_Screen(
    onLogout: () -> Unit,
    onCategoryClick: (String) -> Unit
) {
//    val context = LocalContext.current
//    val auth = FirebaseAuth.getInstance()
//
//    // 1. Instância do Banco de Dados Local
//    val dbLocal = AppDatabase.getDatabase(context)
//    val uid = auth.currentUser?.uid ?: ""
//
//    // 2. Observa o Utilizador no Room em Tempo Real
//    val userLocal by dbLocal.userDao().getUserById(uid).collectAsState(initial = null)
//
//    // 3. Define o nome: Se o Room tiver dados, usa o nome. Se não, usa "Jogador".
//    val userName = userLocal?.name ?: "Jogador"

    Scaffold(

    ) { padding ->

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        color = Laranja,
                        shape = RoundedCornerShape(bottomStart = 45.dp, bottomEnd = 45.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ){

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(
                            "Quiz Master",
                            modifier = Modifier
                                .padding(16.dp,10.dp, 0.dp, 0.dp),
                            color = Color.White,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp
                        )


                        IconButton(
                            onClick = onLogout) {
                            Icon(
                                Icons.AutoMirrored.Filled.ExitToApp,
                                "Sair",
                                modifier = Modifier.size(30.dp),
                                tint = Color.White
                            )
                        }


                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Column(
                            modifier = Modifier
                                .padding(20.dp, 25.dp, 0.dp, 16.dp),
                            verticalArrangement = Arrangement.Center

                        ){
                            Text(
                                text = "Olá",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.SansSerif

                            )
                            Text(
                                modifier = Modifier,
                                text = "Jogador",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            )


                        }


                        Image(
                            painter = painterResource(R.drawable.avatar1),
                            contentDescription = "Perfil",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(130.dp, 130.dp)
                                .padding(0.dp, 0.dp, 20.dp,)
                        )

                    }
                    Text("Escolha um tema para começar:",
                        color = LaranjaLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(20.dp, 8.dp, 0.dp)

                    )
                }


        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp, 200.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
//            Text(
//                "Olá, $userName!",
//                style = MaterialTheme.typography.headlineMedium,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black
//            )
            item{

            }

            item{
                //Spacer(modifier = Modifier.height(24.dp))

//                CardCarrossel()
                Box(
                    modifier = Modifier.padding(0.dp,0.dp)
                ){
                    CategoryCarousel(
                    categories = listOf("Programação", "Esportes", "Política", "TV/Show"),
                    onCategoryClick = { category ->
                        onCategoryClick(category)
                    },
                    modifier = Modifier
                 //       .align(Alignment.TopCenter)
                        .fillMaxWidth(),
                    image = listOf(R.drawable.csharp, R.drawable.newsport, R.drawable.politc, R.drawable.entrent)
                )}


            }
           item {

                   Text(
                       text = "Vença o desafio e ganhe pontos extras",
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(5.dp, 0.dp),
                       fontSize = 18.sp,
                       color = Laranja,
                       fontWeight = FontWeight.SemiBold,
                       fontFamily = FontFamily.SansSerif

                   )

            }

            item{
              //  Spacer(modifier = Modifier.height(10.dp))

                DailyQuizCard(
                    onClickStartDaily = {

                    }
                )

            }



        }
    }
}



@Preview
@Composable
private fun Home_ScreenPreview() {
    MaterialTheme{
       Home_Screen(onLogout = {},
           onCategoryClick = {

           })
//        QuizCard(
//            containerColor = Laranja,
//            imageResId = R.drawable.csharp,
//            title = "Programação",
//            imageDescription = ""
//        )

    }
}