package com.example.quizandroid.ui.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizandroid.R
import com.example.quizandroid.ui.theme.DarkBlue
import com.example.quizandroid.ui.theme.GreySystem
import com.example.quizandroid.ui.theme.Laranja
import com.example.quizandroid.ui.theme.LaranjaLight
import com.example.quizandroid.ui.theme.Purple40
import com.example.quizandroid.ui.theme.StrongBlue

@Composable
fun DailyQuizCard(
    modifier: Modifier = Modifier,
    onClickStartDaily: () -> Unit,
    ) {
    CardDaily(
        onClickStartDaily =  onClickStartDaily
    )
}

@Composable
fun CardDaily(
    modifier: Modifier = Modifier,
    onClickStartDaily: () -> Unit,
    ) {
    var gradient = listOf(LaranjaLight, GreySystem)
    ElevatedCard(
        modifier = Modifier.size(350.dp, 150.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)

    ) {
        Box( //BOX PARA BACKGROUND)
           modifier = Modifier.fillMaxWidth()
                .fillMaxSize()
                .background(brush = Brush.horizontalGradient(gradient))
        ){

               Row(
                   modifier = Modifier.fillMaxWidth()
                       .padding(10.dp, 5.dp, 10.dp),
                   horizontalArrangement = Arrangement.SpaceBetween


               ){
                   Column(
                        modifier = Modifier
                            .padding(10.dp, 0.dp, 0.dp, 0.dp)
                   ){
                       Text(
                           text = "Quiz Diário",
                           modifier = Modifier.
                           padding(5.dp, 25.dp),
                           fontSize = 28.sp,
                           color = DarkBlue,
                           fontWeight = FontWeight.SemiBold,
                           fontFamily = FontFamily.SansSerif
                       )

                       FloatingActionButton(
                           onClick =  onClickStartDaily,
                           modifier = Modifier
                               .size(150.dp,35.dp),
                           containerColor = GreySystem,
                       ) {
                           Text(
                               text = "Começar",
                               color = StrongBlue
                           )
                       }


                   }

                   Image(
                   painter = painterResource(R.drawable.daily),
                   contentDescription = null,
                   modifier = Modifier
                       .size(120.dp,170.dp)
                    )
               }

           }
        }



}

@Preview
@Composable
private fun DailyPreview() {
    MaterialTheme{
        CardDaily(
            onClickStartDaily = {},
        )
    }
}        