package com.example.quizandroid.ui.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.quizandroid.R
import com.example.quizandroid.data.model.UserPrefsManager
import com.example.quizandroid.translateFirebaseError
import com.example.quizandroid.ui.theme.Laranja
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val userPrefs = UserPrefsManager(context)

    val offlineCofre = context.getSharedPreferences("BypassOffline", Context.MODE_PRIVATE)

    val isBiometricEnabled = offlineCofre.getBoolean("biometric_enabled", false)
    val savedEmail = offlineCofre.getString("email", "") ?: ""
    val savedPassword = offlineCofre.getString("password", "") ?: ""

    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid = password.length >= 6
    val canSubmit = isEmailValid && isPasswordValid

    val executeLogin = { targetEmail: String, targetPassword: String ->
        isLoading = true // Assim que isso vira TRUE, a tela esconde os campos!

        auth.signInWithEmailAndPassword(targetEmail, targetPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let { user ->
                        db.collection("users").document(user.uid).get()
                            .addOnSuccessListener { document ->
                                val realName = document.getString("name") ?: "Jogador"
                                userPrefs.saveUser(user.uid, targetEmail, realName)

                                offlineCofre.edit()
                                    .putString("email", targetEmail)
                                    .putString("password", targetPassword)
                                    .putString("uid", user.uid)
                                    .putString("name", realName)
                                    .apply()

                                isLoading = false
                                onLoginSuccess()
                            }
                            .addOnFailureListener {
                                userPrefs.saveUser(user.uid, targetEmail, "Jogador")

                                offlineCofre.edit()
                                    .putString("email", targetEmail)
                                    .putString("password", targetPassword)
                                    .putString("uid", user.uid)
                                    .putString("name", "Jogador")
                                    .apply()

                                isLoading = false
                                onLoginSuccess()
                            }
                    }
                } else {
                    val savedUid = offlineCofre.getString("uid", "")
                    val savedName = offlineCofre.getString("name", "Jogador")

                    if (targetEmail.equals(savedEmail, ignoreCase = true) && targetPassword == savedPassword && !savedUid.isNullOrEmpty()) {
                        userPrefs.saveUser(savedUid, targetEmail, savedName!!)
                        isLoading = false
                        onLoginSuccess()
                    } else {
                        val exception = task.exception
                        val errorMsg = translateFirebaseError(exception)
                        isLoading = false
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    val performLogin = {
        if (canSubmit) {
            executeLogin(email, password)
        }
    }

    val triggerBiometricLogin = {
        if (activity != null && savedEmail.isNotEmpty() && savedPassword.isNotEmpty()) {
            val executor = ContextCompat.getMainExecutor(activity)
            val biometricPrompt = BiometricPrompt(activity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        // Removido o preenchimento fantasma! Joga direto para o login.
                        executeLogin(savedEmail, savedPassword)
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login Biométrico")
                .setSubtitle("Use sua digital ou rosto para entrar")
                .setNegativeButtonText("Usar Senha")
                .build()

            biometricPrompt.authenticate(promptInfo)
        } else {
            Toast.makeText(context, "Faça login com sua senha uma vez para usar a biometria.", Toast.LENGTH_LONG).show()
        }
    }

    var hasPrompted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (isBiometricEnabled && !hasPrompted && savedEmail.isNotEmpty() && savedPassword.isNotEmpty()) {
            hasPrompted = true
            triggerBiometricLogin()
        }
    }

    val handleResetPassword = {
        if (isEmailValid) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Link enviado para $email", Toast.LENGTH_LONG).show()
                    } else {
                        val errorMsg = translateFirebaseError(task.exception)
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(context, "Digite um e-mail válido", Toast.LENGTH_SHORT).show()
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
            elevation = CardDefaults.cardElevation(
                defaultElevation = 24.dp,
                pressedElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bem-vindo!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Laranja
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- MAGIA AQUI: Se estiver carregando, mostra apenas o spinner. ---
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Laranja, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Autenticando...", color = Color.Gray, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    // Se não estiver carregando, mostra os campos normalmente
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("E-mail") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = Laranja) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Laranja,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = Laranja
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Senha") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Laranja) },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Laranja,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = Laranja
                        )
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { handleResetPassword() }) {
                            Text("Esqueci minha senha", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { performLogin() },
                        enabled = canSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canSubmit) Laranja else Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text("ENTRAR", fontWeight = FontWeight.Bold)
                    }

                    if (isBiometricEnabled && savedEmail.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { triggerBiometricLogin() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Laranja),
                            border = BorderStroke(1.dp, Laranja)
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = "Biometria", modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ENTRAR COM BIOMETRIA", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onNavigateToRegister) {
                        Text("Não tem uma conta? Cadastre-se", color = Laranja, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}