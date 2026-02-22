package com.example.quizandroid.ui.quizCategory


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.quizandroid.ui.theme.DarkBlue
import com.example.quizandroid.ui.theme.Laranja
import com.example.quizandroid.ui.theme.Purple40
import com.example.quizandroid.ui.theme.StrongBlue

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onOpenQuiz: (String) -> Unit
) {
    ListContent(
        modifier = modifier,
        onBack = onBack,
        onOpenQuiz = onOpenQuiz
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onOpenQuiz: (String) -> Unit
    ) {
    Scaffold(
        modifier = Modifier,
        topBar ={
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Quizes",
                        modifier = Modifier,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center

                    )},
                colors = TopAppBarDefaults.topAppBarColors(Laranja),
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            modifier = Modifier.size(34.dp),
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                }

            )}

    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 100.dp,20.dp, 0.dp)

        ){
            Text(
                text = "Teste seus conhecimentos em: ",
                color = Laranja,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        }
        LazyColumn(
            modifier = Modifier
                .padding(20.dp, 150.dp,20.dp,20.dp)
                .fillMaxWidth()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            item{
                CardCat(
                    title = "Lógica de Programação",
                    contentColor = DarkBlue,
                    onClick = {onOpenQuiz("logica")}
                    )

            }
            item{
                Spacer(modifier = Modifier.height(24.dp,))
                CardCat(
                    title = "Banco de Dados",
                    contentColor = StrongBlue,
                    onClick = {onOpenQuiz("bd")}
                )
            }
            item{
                Spacer(modifier = Modifier.height(24.dp,))
                CardCat(
                    title = "Alg. e Programação I",
                    contentColor = DarkBlue,
                    onClick = {onOpenQuiz("algI")})

            }
            item{
                Spacer(modifier = Modifier.height(24.dp,))
                CardCat(
                    title = "Alg. e Programação II",
                    contentColor = StrongBlue,
                    onClick = {onOpenQuiz("algII")}
                )
            }
            item{
                Spacer(modifier = Modifier.height(24.dp,))
                CardCat(
                    title = "Dev. Mobile",
                    contentColor = DarkBlue,
                    onClick = {onOpenQuiz("pdm")}
                )

            }
            item{
                Spacer(modifier = Modifier.height(24.dp,))
                CardCat(
                    title = "Dev. de Software",
                    contentColor = StrongBlue,
                    onClick = {onOpenQuiz("pds")}
                )
            }
            item{
                Spacer(modifier = Modifier.height(24.dp,))
                CardCat(
                    title = "Redes de Internet",
                    contentColor = DarkBlue,
                    onClick = {onOpenQuiz("Redes")}
                )

            }
            item{
                Spacer(modifier = Modifier.height(24.dp,))
                CardCat(
                    title = "Arquitetura ",
                    contentColor = StrongBlue,
                    onClick = {onOpenQuiz("arquitetura")}
                )
            }
        }
    }
}

@Composable
fun CardCat(
    modifier: Modifier = Modifier,
    title : String,
    contentColor: Color,
    onClick: () -> Unit
    )
{
    ElevatedCard(
        onClick = onClick, //IR PARA A TELA DE QUIZQUESTION COM A BUSCA DO BD DAS QUESTÕES CORRETAS
        modifier = Modifier.size(300.dp, 70.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = contentColor)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .padding(20.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,


        ){
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Ir",
                tint = Color.White
            )
        }
    }
}

@Preview
@Composable
private fun ListScreenPreview() {
    MaterialTheme{
//        CardCat(
//            title = "Copa do mundo de 94"
//        )
        ListContent(
            onBack={},
        onOpenQuiz={}
        )
    }
}