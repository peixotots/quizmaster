package com.example.quizandroid.ui.quizCategory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizandroid.R
import com.example.quizandroid.ui.theme.DarkBlue
import com.example.quizandroid.ui.theme.GreySystem
import com.example.quizandroid.ui.theme.Purple40
import com.example.quizandroid.ui.theme.Laranja
import com.example.quizandroid.ui.theme.LaranjaLight
import kotlin.String


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardCarrossel(modifier: Modifier = Modifier) {

    val items =
        listOf(
            QuizCardData(
                R.drawable.csharp,
                "Linguagens",
                Color.Black,
                imageSize = modifier
                    .fillMaxWidth()
                    .padding()
                    .size(90.dp, 90.dp)
            ),
            QuizCardData(
                R.drawable.newsport,
                "Esportes",
                Purple40,
                imageSize = modifier
            ),
            QuizCardData(
                R.drawable.politc,
                "Política",
                Color.Green,
                imageSize = modifier

            ),
            QuizCardData(
                R.drawable.entrent,
                "Entretenimento",
                Color.Red,
                imageSize = modifier
            )

        )
    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { items.size },
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 8.dp)

    ) { i ->
        val item = items[i]
        QuizCard(
            imageSize = Modifier,
            containerColor = item.color,
            imageResId = item.imageResId,
            title = item.title,
            imageDescription = null

        )
    }

}

data class QuizCardData(
    val imageResId: Int,
    val title: String,
    val color: Color,
    val imageSize: Modifier
)

@Composable
fun QuizCard(
    onClick: () -> Unit = {},

    containerColor: Color = MaterialTheme.colorScheme.surface,
    imageResId: Int, // id da imagem no res.drawable
    title: String, // título que vai no card
    imageDescription: String?, // se precisar colocar uma descrição, mas pode ser null,
    imageSize: Modifier

) {
    ElevatedCard(
        modifier = Modifier.size(150.dp, 180.dp),
        shape = CardDefaults.elevatedShape,
        colors = CardDefaults.elevatedCardColors(containerColor),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 8.dp
        ),
    ) {

        Column() {
            Box(
                modifier = Modifier
                    .padding(30.dp, 10.dp, 5.dp, 0.dp)
                    .size(100.dp, 120.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = imageSize,
                    contentScale = ContentScale.FillWidth
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif
                )
            }

        }
//        Row(
//            modifier = Modifier
//                .padding(30.dp, 10.dp, 5.dp, 0.dp),
//            horizontalArrangement = Arrangement.Absolute.Right
//        ){
//        }


    }

}

@Composable
fun TesteTopBar(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    color = Laranja,
                    shape = RoundedCornerShape(bottomStart = 45.dp, bottomEnd = 45.dp)
                )
        ) {
            Column(){
            Text(
                text = "Olá",
                color = Color.White
            )
            Text(
                modifier = Modifier.padding(20.dp),
                text = "Jogador",
                color = Color.White
            )

            }
        }
        CategoryCarousel(
            categories = listOf("Programação", "Esportes", "Política", "TV/Show"),
            onCategoryClick = { category ->
                // Preview não precisa de ação
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(16.dp, 200.dp, 12.dp,),
            image = listOf(R.drawable.csharp, R.drawable.newsport, R.drawable.politc, R.drawable.entrent)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCarouselScreen(
    onCategoryClick: (String)-> Unit
){
    CategoryCarousel(
        categories = listOf("Programação", "Esportes", "Política", "TV/Show"),
        onCategoryClick = { category ->
            onCategoryClick(category)
        },
        modifier = Modifier
           // .align(Alignment.TopCenter)
            .fillMaxWidth(),
        image = listOf(R.drawable.csharp, R.drawable.newsport, R.drawable.politc, R.drawable.entrent)
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCarousel(
    categories: List<String>,
    image: List<Int>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,

) {
    val carouselState = rememberCarouselState { categories.size }

    HorizontalMultiBrowseCarousel(
        state = carouselState,
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)  // Ajuste conforme imagem
            .padding(vertical = 5.dp)
            .background(color = GreySystem,
                shape = RoundedCornerShape(18.dp)),
        preferredItemWidth = 100.dp,  // Tamanho dos cards como na imagem
        itemSpacing = 12.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { index ->
        val category = categories[index] //CATEGORIA ESPECÍFICA
        val imageResId = image[index] // IMAGEM ESPECÍFICA
        Card(
            onClick = { onCategoryClick(category) },
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f),  // Quadrado como ícones da imagem
            colors = CardDefaults.cardColors(
                containerColor = LaranjaLight
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {

                Box(
                    modifier = Modifier.fillMaxSize()
                    ) {
                    // Ícone ou texto da categoria
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = category,
                        modifier = Modifier
                            .size(70.dp)
                            .padding(5.dp, 5.dp, 5.dp),
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        modifier = Modifier
                            .padding(5.dp)
                            .align(Alignment.BottomEnd),
                        text = category,
                        color = DarkBlue,
                        style = MaterialTheme.typography.labelLarge,

                        )
                }


        }
    }
}


@Preview
@Composable
private fun CorouselPreview() {
    MaterialTheme {
        //CardCarrossel()
        TesteTopBar()
//        CategoryCarousel(
//            categories = listOf("Inbox", "Map", "Chats", "Report", "Tips", "Settings"),
//            onCategoryClick = { category ->
//                // Preview não precisa de ação
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        )
    }
}