package com.example.quizandroid.ui.quizCategory

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Forward
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.quizandroid.R
import com.example.quizandroid.ui.theme.DarkBlue
import com.example.quizandroid.ui.theme.GreySystem


import com.example.quizandroid.ui.theme.Laranja
import com.example.quizandroid.ui.theme.LaranjaLight
import com.example.quizandroid.ui.theme.Purple40
import com.example.quizandroid.ui.theme.Purple80
import com.example.quizandroid.ui.theme.PurpleGrey40
import com.example.quizandroid.ui.theme.PurpleGrey80
import com.example.quizandroid.ui.theme.StrongBlue


@Composable
fun GenericQuizScreen(
    modifier: Modifier = Modifier,
    category: String,
    onBack: () -> Unit
) {

    GenericQuizContent(
        modifier = modifier,
        category = category,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericQuizContent(
    modifier: Modifier = Modifier,
    category: String,
    onBack: () -> Unit

) {
    Scaffold(
        modifier = Modifier,
        topBar ={
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Programação",
                        modifier = Modifier,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center

                    )},
                colors = TopAppBarDefaults.topAppBarColors(Laranja),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            modifier = Modifier.size(34.dp),
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                }

            )}
    ) {padding->
        Box(
        modifier = Modifier
            .padding(10.dp, 120.dp, 10.dp)
            .fillMaxSize()
    ) {
        // Imagem de fundo no BottomEnd (primeiro, fica atrás)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp), // Ajuste para não colidir com botões
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                painter = painterResource(R.drawable.orangequest),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(120.dp, 200.dp) // Tamanho fixo para controle
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                TitleCard(
                    imageResId = 0,
                    title = "Considere a expressão lógica: \n" +
                            "A∧B. Em uma tabela verdade, em qual situação o resultado dessa expressão será verdadeiro?",
                    background = Laranja,
                    titleColor = Color.White
                )
            }//Pergunta

            item {
                Spacer(modifier = Modifier.height(24.dp,))
                Box(
                    modifier = Modifier.padding(0.dp, 16.dp),
                    contentAlignment = Alignment.Center
                ) {


                    itemCard(
                        modifier = Modifier.padding(24.dp, 0.dp),
                        title = " A)",
                        description = "A e B são verdadeiros"
                    )
                }

                Box(
                    modifier = Modifier.padding(0.dp, 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Spacer(modifier = Modifier.height(16.dp,))

                    itemCard(
                        modifier = Modifier.padding(24.dp, 0.dp),
                        title = " B)",
                        description = "A falso e B Verdadeiro"
                    )
                }
                Box(
                    modifier = Modifier.padding(0.dp, 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Spacer(modifier = Modifier.height(16.dp,))

                    itemCard(
                        modifier = Modifier.padding(24.dp, 0.dp),
                        title = " C)",
                        description = "A e B são falsos"
                    )
                }
                Box(
                    modifier = Modifier.padding(0.dp, 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Spacer(modifier = Modifier.height(16.dp,))

                    itemCard(
                        modifier = Modifier.padding(24.dp, 0.dp),
                        title = " D)",
                        description = "A verdadeiro e B falso"
                    )
                }
            }//Conjunto de respostas

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp, 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween


                ) {
                    FloatingActionButton(
                        onClick = {},//COLOCAR O CAMINHO DE RETORNAR PARA A QUESTÃO ANTERIOR OU AVANÇAR
                        modifier = Modifier.size(120.dp, 40.dp),
                        containerColor = GreySystem,
                        contentColor = contentColorFor(Purple40)
                    ) {
                        Text(
                            text = "Anterior",
                            fontSize = 18.sp,
                            color = Laranja
                        )
                    }

                    FloatingActionButton(
                        onClick = {},//COLOCAR O CAMINHO DE RETORNAR PARA A QUESTÃO ANTERIOR OU AVANÇAR
                        modifier = Modifier.size(120.dp, 40.dp),
                        containerColor = GreySystem,
                        contentColor = contentColorFor(Purple40)
                    ) {
                        Text(
                            text = "Próxima",
                            fontSize = 18.sp,
                            color = Laranja
                        )
                    }

                }

            }//botões


        }
    }
    }




}

@Composable
fun TitleCard(
    modifier: Modifier = Modifier,

    imageResId: Int,
    title : String,
    background: Color,
    titleColor : Color
)
{
    ElevatedCard(
        modifier = Modifier
            .size(350.dp, 150.dp),
        shape = CardDefaults.elevatedShape,
elevation = (CardDefaults.cardElevation(
            defaultElevation = 8.dp)),
        colors = CardDefaults.cardColors(background)
    )
    {


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()


        ){

            LazyColumn(
                modifier = Modifier
                    .padding(2.dp, 0.dp)
                    .fillMaxSize()
                    .align(Alignment.Center),
                state = rememberLazyListState(),
                verticalArrangement = Arrangement.Center
            )
            {

                item{
                    Text(
                        text = title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(),
                        color = titleColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(
                            letterSpacing = 0.1.em,
                        ),
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    )
                }

            }

        }
    }
}

@Composable
fun itemCard(modifier: Modifier = Modifier,
             title: String,
             description : String
             )
{
    ElevatedCard(
        onClick = {}, // GERAR SE A RESPOSTA É CERTA OU NÃO
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .size(width = 300.dp, height = 60.dp),
        colors = CardDefaults.cardColors(
            containerColor = LaranjaLight,
        ),
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .fillMaxSize(),
            horizontalArrangement= Arrangement.spacedBy(30.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title, // ALTERNATIVA
                color = StrongBlue,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp

            )
            Text(
                text = description, // RESPOSTA
                color = StrongBlue,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        }
    }
}
@Preview
@Composable
private fun QuestionPreview() {

    MaterialTheme{
        //GenericQuizScreen()
//        TitleCard(
//            modifier = Modifier,
//
//            imageResId = 0,
//            title = "A linguagem Kotlin foi desenvolvida por qual empresa de tecnologia e deriva de qual outra linguagem?"
//        )
        GenericQuizContent(
            category = "",
            onBack= {  }
        )
    }
}