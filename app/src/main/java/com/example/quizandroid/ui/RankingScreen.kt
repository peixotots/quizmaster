package com.example.quizandroid.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.google.firebase.firestore.Source // Importante para forçar a rede
import kotlinx.coroutines.tasks.await

data class RankingUser(val id: String, val name: String, val score: Int, val avatar: String)

@Composable
fun RankingScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var users by remember { mutableStateOf<List<RankingUser>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // LaunchedEffect com Unit roda toda vez que a tela é aberta
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val db = FirebaseFirestore.getInstance()

            // FORÇANDO A BUSCA NA REDE (SERVER) PARA IGNORAR O CACHE LOCAL
            val snapshot = db.collection("users")
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(50)
                .get(Source.SERVER) // <-- AQUI ESTÁ O SEGREDO
                .await()

            users = snapshot.documents.mapNotNull { doc ->
                val name = doc.getString("name") ?: "Jogador"
                val score = doc.getLong("score")?.toInt() ?: 0
                val avatar = doc.getString("avatar") ?: "👤"
                RankingUser(doc.id, name, score, avatar)
            }
        } catch (e: Exception) {
            // Se falhar a rede (ex: sem internet), tenta o cache como plano B
            try {
                val db = FirebaseFirestore.getInstance()
                val snapshot = db.collection("users")
                    .orderBy("score", Query.Direction.DESCENDING)
                    .limit(50)
                    .get(Source.CACHE)
                    .await()

                users = snapshot.documents.mapNotNull { doc ->
                    val name = doc.getString("name") ?: "Jogador"
                    val score = doc.getLong("score")?.toInt() ?: 0
                    val avatar = doc.getString("avatar") ?: "👤"
                    RankingUser(doc.id, name, score, avatar)
                }
            } catch (innerE: Exception) {
                innerE.printStackTrace()
            }
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FE),
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    selected = false,
                    onClick = onNavigateToHome,
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.EmojiEvents, null) },
                    selected = true,
                    onClick = { /* Já estamos no ranking */ },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Laranja)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    selected = false,
                    onClick = onNavigateToProfile,
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Leaderboard",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(32.dp))

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
                    if (users.size > 1) PodiumAvatar(user = users[1], rank = 2, size = 80, color = Color(0xFFBDBDBD))
                    PodiumAvatar(user = users[0], rank = 1, size = 110, color = Color(0xFFFFD700), isWinner = true)
                    if (users.size > 2) PodiumAvatar(user = users[2], rank = 3, size = 80, color = Color(0xFFCD7F32))
                }

                Spacer(modifier = Modifier.height(32.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val remainingUsers = if (users.size > 3) users.subList(3, users.size) else emptyList()
                    itemsIndexed(remainingUsers) { index, user ->
                        val rank = index + 4
                        RankingListItem(user = user, rank = rank)
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Nenhum jogador encontrado.", color = Color.Gray)
                }
            }
        }
    }
}

// ... (Componentes PodiumAvatar e RankingListItem permanecem iguais)
@Composable
fun PodiumAvatar(user: RankingUser, rank: Int, size: Int, color: Color, isWinner: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isWinner) {
            Icon(Icons.Default.EmojiEvents, null, tint = color, modifier = Modifier.size(32.dp).offset(y = 8.dp))
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
                    Text(text = user.name.take(1).uppercase(), color = Color.DarkGray, fontSize = (size / 2.5).sp, fontWeight = FontWeight.Bold)
                } else {
                    Text(text = user.avatar, fontSize = (size / 1.8).sp)
                }
            }
            Surface(shape = CircleShape, color = color, modifier = Modifier.offset(y = 12.dp).size(28.dp), shadowElevation = 2.dp) {
                Box(contentAlignment = Alignment.Center) {
                    Text("${rank}º", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(text = user.name.split(" ").first(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp)) {
            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
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
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${rank}º", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray, modifier = Modifier.width(36.dp))
            Box(modifier = Modifier.size(46.dp).clip(CircleShape).background(Color(0xFFF0F0F0)), contentAlignment = Alignment.Center) {
                if (user.avatar == "👤") {
                    Text(text = user.name.take(1).uppercase(), color = Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                } else {
                    Text(text = user.avatar, fontSize = 26.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = user.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.weight(1f), color = Color.DarkGray)
            Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Stars, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${user.score}", fontWeight = FontWeight.Bold, color = Color.DarkGray, fontSize = 14.sp)
                }
            }
        }
    }
}