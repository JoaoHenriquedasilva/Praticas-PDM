package com.sistemaestudos

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sistemaestudos.ui.theme.SistemaestudosTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SistemaestudosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun RegisterPage(modifier: Modifier = Modifier) {
    val activity = LocalContext.current as Activity

    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Criar Nova Conta", fontSize = 24.sp)
        Spacer(modifier = Modifier.size(16.dp))

        // Campos do formulário (Passo 2)
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Nome de usuário") }, modifier = Modifier.fillMaxWidth(0.9f))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail") }, modifier = Modifier.fillMaxWidth(0.9f))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(0.9f))
        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Repetir Senha") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(0.9f))

        Spacer(modifier = Modifier.size(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    Toast.makeText(activity, "Registro confirmado!", Toast.LENGTH_SHORT).show()
                    activity.finish()
                },
                enabled = username.isNotEmpty() && email.isNotEmpty() &&
                        password.isNotEmpty() && password == confirmPassword
            ) {
                Text("Registrar")
            }

            Button(onClick = { username = ""; email = ""; password = ""; confirmPassword = "" }) {
                Text("Limpar")
            }
        }
    }
}