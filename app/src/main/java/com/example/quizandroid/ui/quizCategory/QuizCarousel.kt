package com.example.quizandroid.ui.quizCategory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quizandroid.R
import com.example.quizandroid.ui.theme.DarkBlue
import com.example.quizandroid.ui.theme.GreySystem
import com.example.quizandroid.ui.theme.LaranjaLight
import kotlin.String

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCarouselScreen(
    onCategoryClick: (String) -> Unit
) {
    CategoryCarousel(
        categories = listOf("Programação", "Esportes", "Política", "TV/Show"),
        onCategoryClick = { category ->
            onCategoryClick(category)
        },
        modifier = Modifier
            .fillMaxWidth(),
        image = listOf(
            R.drawable.csharp,
            R.drawable.newsport,
            R.drawable.politc,
            R.drawable.entrent
        )
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
            .background(
                color = GreySystem,
                shape = RoundedCornerShape(18.dp)
            ),
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


    }
}