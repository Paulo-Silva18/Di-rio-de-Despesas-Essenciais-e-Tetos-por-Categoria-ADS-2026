package br.edu.iftm.diariodespesas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import br.edu.iftm.diariodespesas.viewmodel.AuthState

@Composable
fun AuthScreen(state: AuthState, isRegister: Boolean, onSubmit: (String, String) -> Unit, onToggle: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Text(if (isRegister) "Criar conta" else "Entrar", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = email, onValueChange = { email = it }, label = { Text("E-mail") },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = pass, onValueChange = { pass = it }, label = { Text("Senha (mín. 6 caracteres)") },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = { onSubmit(email.trim(), pass) },
            enabled = !state.loading, modifier = Modifier.fillMaxWidth()
        ) {
            if (state.loading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            else Text(if (isRegister) "Cadastrar" else "Entrar")
        }
        TextButton(onClick = onToggle) {
            Text(if (isRegister) "Já tenho conta" else "Não tenho conta — cadastrar")
        }
    }
}
