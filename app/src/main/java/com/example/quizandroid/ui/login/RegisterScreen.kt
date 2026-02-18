package com.example.quizandroid.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.quizandroid.R
import com.example.quizandroid.data.model.AppDatabase
import com.example.quizandroid.data.model.UserEntity
import com.example.quizandroid.data.model.UserPrefsManager
import com.example.quizandroid.translateFirebaseError
import com.example.quizandroid.ui.theme.Laranja
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var name by remember { mutableStateOf("teste4") }
    var email by remember { mutableStateOf("teste4@teste.com") }
    var password by remember { mutableStateOf("123456") }
    var confirmPassword by remember { mutableStateOf("123456") }

    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Instância do banco de dados local
    val userDao = AppDatabase.getDatabase(context).userDao()
    val userPrefs = UserPrefsManager(context)

    val passwordsMatch = password == confirmPassword && password.isNotEmpty()
    val isFormValid = name.isNotEmpty() &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
            password.length >= 6 &&
            passwordsMatch

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedBorderColor = Laranja,
        unfocusedBorderColor = Color.LightGray,
        focusedLabelColor = Laranja,
        unfocusedLabelColor = Color.Gray
    )

    val performRegister = {
        if (isFormValid) {
            isLoading = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val userProfile = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "score" to 0,
                            "quizzesDone" to 0,
                            "createdAt" to FieldValue.serverTimestamp()
                        )

                        userId?.let { uid ->
                            // 1. Gravação Remota (Firestore)
                            db.collection("users").document(uid).set(userProfile)
                                .addOnSuccessListener {
                                    // 2. Gravação Local (Room + SharedPreferences)
                                    scope.launch {
                                        try {
                                            userDao.insertUser(
                                                UserEntity(
                                                    uid = uid,
                                                    name = name,
                                                    email = email,
                                                    totalScore = 0,
                                                    quizzesDone = 0
                                                )
                                            )
                                            userPrefs.saveUser(uid, email, name)

                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Conta criada com sucesso!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onRegisterSuccess()
                                        } catch (e: Exception) {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Erro local: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    Toast.makeText(
                                        context,
                                        "Erro ao sincronizar: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        isLoading = false
                        val mensagemAmigavel = translateFirebaseError(task.exception)
                        Toast.makeText(context, mensagemAmigavel, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_quiz),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Crie sua conta para jogar",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Laranja
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = Laranja) },
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = Laranja) },
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Laranja) },
                    trailingIcon = {
                        val image =
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(image, null, tint = Color.Gray)
                        }
                    },
                    colors = fieldColors,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Senha") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Laranja) },
                    trailingIcon = {
                        val image =
                            if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(image, null, tint = Color.Gray)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = if (passwordsMatch || confirmPassword.isEmpty()) Laranja else Color.Red,
                        unfocusedBorderColor = if (passwordsMatch || confirmPassword.isEmpty()) Color.LightGray else Color.Red,
                        focusedLabelColor = if (passwordsMatch || confirmPassword.isEmpty()) Laranja else Color.Red,
                        unfocusedLabelColor = Color.Gray
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = Laranja)
                } else {
                    Button(
                        onClick = { performRegister() },
                        enabled = isFormValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFormValid) Laranja else Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text("CRIAR CONTA", fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = onNavigateToRegister) {
                    Text("Voltar para o Login", color = Laranja)
                }
            }
        }
    }
}