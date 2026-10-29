package br.edu.iftm.diariodespesas.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthState(
    val loading: Boolean = false,
    val error: String? = null,
    val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(email: String, password: String) = authenticate(email, password, register = false)
    fun register(email: String, password: String) = authenticate(email, password, register = true)

    private fun authenticate(email: String, password: String, register: Boolean) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { it.copy(error = "E-mail inválido.") }; return
        }
        if (password.length < 6) {
            _state.update { it.copy(error = "A senha deve ter ao menos 6 caracteres.") }; return
        }
        _state.update { it.copy(loading = true, error = null) }
        val task = if (register) auth.createUserWithEmailAndPassword(email, password)
        else auth.signInWithEmailAndPassword(email, password)
        task.addOnCompleteListener { t ->
            if (t.isSuccessful) {
                _state.update { it.copy(loading = false, userId = auth.currentUser?.uid) }
            } else {
                val msg = if (register) "Não foi possível cadastrar: ${t.exception?.localizedMessage}"
                else "E-mail ou senha incorretos."
                _state.update { it.copy(loading = false, error = msg) }
            }
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }

    fun logout() {
        auth.signOut()
        _state.update { AuthState(userId = null) }
    }
}
