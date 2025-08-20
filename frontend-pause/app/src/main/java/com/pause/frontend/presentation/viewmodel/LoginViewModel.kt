package com.pause.frontend.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pause.frontend.data.local.SessionPrefs
import com.pause.frontend.data.repository.AuthRepository
import com.pause.frontend.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val valid: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
    val done: Boolean = false
)

class LoginViewModel(
    app: Application,
    private val repo: AuthRepository = AuthRepositoryImpl(),
) : AndroidViewModel(app) {

    private val prefs = SessionPrefs(app)

    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui

    fun onEmailChange(value: String) {
        _ui.value = _ui.value.copy(email = value, valid = isValidEmail(value), error = null)
    }

    fun createProfile() {
        val email = _ui.value.email.trim()
        if (!isValidEmail(email)) {
            _ui.value = _ui.value.copy(error = "Correo inválido (usa formato *@*.*)")
            return
        }
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            val r = repo.createUserAndPet(email)
            _ui.value = r.fold(
                onSuccess = { (user, pet) ->
                    viewModelScope.launch {
                        prefs.save(email = email, userId = user.id, petId = pet.id)
                    }
                    _ui.value.copy(loading = false, done = true)
                },
                onFailure = { e ->
                    _ui.value.copy(loading = false, error = e.message ?: "Error")
                }
            )
        }
    }

    private fun isValidEmail(s: String): Boolean =
        Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(s) // *@*.* simple
}