package com.example.quizandroid.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizandroid.ui.theme.Laranja
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class RankingUser(val id: String, val name: String, val score: Int, val avatar: String)

@Composable
fun RankingScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var users by remember { mutableStateOf<List<RankingUser>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("users")
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(50)

        val listener =
            query.addSnapshotListener(com.google.firebase.firestore.MetadataChanges.INCLUDE) { snapshot, e ->
                if (snapshot != null) {
                    users = snapshot.documents.mapNotNull { doc ->
                        val name = doc.getString("name") ?: "Jogador"
                        val score = doc.getLong("score")?.toInt() ?: 0
                        val avatar = doc.getString("avatar") ?: "👤"
                        RankingUser(doc.id, name, score, avatar)
                    }
                    isLoading = false
                } else if (e != null) {
                    isLoading = false
                }
            }

        onDispose { listener.remove() }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FE),
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val navColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    indicatorColor = Laranja,
                    unselectedIconColor = Color.Gray
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    selected = false,
                    onClick = onNavigateToHome,
                    colors = navColors
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.EmojiEvents, null) },
                    selected = true,
                    onClick = { /* Já estamos no ranking */ },
                    colors = navColors
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    selected = false,
                    onClick = onNavigateToProfile,
                    colors = navColors
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Surface(
                color = Laranja,
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ranking",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (isLoading) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Laranja)
                    }
                } else if (users.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (users.size > 1) PodiumAvatar(
                            user = users[1],
                            rank = 2,
                            size = 80,
                            color = Color(0xFFBDBDBD)
                        )
                        PodiumAvatar(
                            user = users[0],
                            rank = 1,
                            size = 110,
                            color = Color(0xFFFFD700),
                            isWinner = true
                        )
                        if (users.size > 2) PodiumAvatar(
                            user = users[2],
                            rank = 3,
                            size = 80,
                            color = Color(0xFFCD7F32)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        val remainingUsers =
                            if (users.size > 3) users.subList(3, users.size) else emptyList()
                        itemsIndexed(remainingUsers) { index, user ->
                            val rank = index + 4
                            RankingListItem(user = user, rank = rank)
                        }
                    }
                } else {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("Aguardando pontuações...", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumAvatar(user: RankingUser, rank: Int, size: Int, color: Color, isWinner: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isWinner) {
            Icon(
                Icons.Default.EmojiEvents,
                null,
                tint = color,
                modifier = Modifier
                    .size(32.dp)
                    .offset(y = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(32.dp))
        }
        Box(contentAlignment = Alignment.BottomCenter) {
            Box(
                modifier = Modifier
                    .size(size.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0))
                    .border(width = 3.dp, color = color, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (user.avatar == "👤") {
                    Text(
                        text = user.name.take(1).uppercase(),
                        color = Color.DarkGray,
                        fontSize = (size / 2.5).sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(text = user.avatar, fontSize = (size / 1.8).sp)
                }
            }
            Surface(
                shape = CircleShape,
                color = color,
                modifier = Modifier
                    .offset(y = 12.dp)
                    .size(28.dp),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "${rank}º",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = user.name.split(" ").first(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp)) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Stars, null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${user.score}", color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun RankingListItem(user: RankingUser, rank: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${rank}º",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.width(36.dp)
            )
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                if (user.avatar == "👤") {
                    Text(
                        text = user.name.take(1).uppercase(),
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                } else {
                    Text(text = user.avatar, fontSize = 26.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = user.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
                color = Color.DarkGray
            )
            Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp)) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Stars,
                        null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${user.score}",
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}