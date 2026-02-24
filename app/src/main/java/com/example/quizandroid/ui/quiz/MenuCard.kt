package com.example.quizandroid.ui.quiz



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quizandroid.ui.theme.Laranja
import com.example.quizandroid.ui.theme.LaranjaLight

@Composable
fun MenuCard(modifier: Modifier = Modifier) {
    CardContent(
        title = "Ranking",
        image =
            Icons.Filled.Leaderboard

    
    )
}

@Preview
@Composable
private fun MenuCardPreview() {
        MaterialTheme{
            MenuCard()

        }
}

@Composable
fun RowCards(modifier: Modifier = Modifier) {

    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(0.dp, 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CardContent(
                title = "Criar",
                image = Icons.Filled.AddCircleOutline
            )
            CardContent(
                title = "Ranking",
                image = Icons.Filled.Leaderboard
            )
            CardContent(
                title = "Tarefa",
                image = Icons.Filled.TaskAlt
            )

            CardContent(
                title = "Perfil",
                image = Icons.Filled.PersonOutline
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(0.dp, 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CardContent(
                title = "+",
                image = Icons.Filled.Add
            )
            CardContent(
                title = "+",
                image = Icons.Filled.Add
            )
            CardContent(
                title = "+",
                image = Icons.Filled.Add
            )

            CardContent(
                title = "+",
                image = Icons.Filled.Add
            )

        }
    }
}


@Composable
fun CardContent(
    modifier: Modifier = Modifier,
    title: String,
    image: ImageVector
    ) {

    ElevatedCard(
        onClick = {},
        modifier = Modifier
            .size(70.dp, 60.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        colors =CardDefaults.elevatedCardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

        ){
                Icon(
                    imageVector = image,
                    contentDescription = null,
                    tint = Laranja
                )



                Text(
                    text = title,
                    color = Laranja
                )
                

        }


    }


}