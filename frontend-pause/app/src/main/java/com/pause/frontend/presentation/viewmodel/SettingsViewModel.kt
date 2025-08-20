package com.pause.frontend.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pause.frontend.data.local.SessionPrefs
import com.pause.frontend.data.remote.dto.UserDetailResponse
import com.pause.frontend.data.repository.SettingsRepository
import com.pause.frontend.data.repository.SettingsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val details: UserDetailResponse? = null,
    val userNameInput: String = "",
    val petNameInput: String = "",
    val savingUser: Boolean = false,
    val savingPet: Boolean = false,
    val deleting: Boolean = false,
    val doneLogout: Boolean = false,
    val doneDeleted: Boolean = false,
    val savedUser: Boolean = false,
    val savedPet: Boolean = false
)

class SettingsViewModel(
    app: Application,
    private val repo: SettingsRepository = SettingsRepositoryImpl(),
) : AndroidViewModel(app) {

    private val prefs = SessionPrefs(app)
    private var userId: Long = -1L
    private var petId: Long? = null

    private val _ui = MutableStateFlow(SettingsUiState())
    val ui: StateFlow<SettingsUiState> = _ui

    fun load(userId: Long, petId: Long?) {
        this.userId = userId
        this.petId = petId
        _ui.value = SettingsUiState(loading = true)
        viewModelScope.launch {
            val r = repo.getDetails(userId)
            _ui.value = r.fold(
                onSuccess = { d ->
                    SettingsUiState(
                        loading = false,
                        details = d,
                        userNameInput = d.user.userName,
                        petNameInput = d.pet?.petName ?: ""
                    )
                },
                onFailure = { SettingsUiState(loading = false, error = it.message) }
            )
        }
    }

    fun onUserNameChange(s: String) { _ui.value = _ui.value.copy(userNameInput = s) }
    fun onPetNameChange(s: String) { _ui.value = _ui.value.copy(petNameInput = s) }

    fun saveUserName() {
        val st = _ui.value
        val d = st.details ?: return
        val newName = st.userNameInput.trim()
        if (newName.isBlank()) { _ui.value = st.copy(error = "El nombre de usuario no puede estar vacío"); return }
        viewModelScope.launch {
            _ui.value = st.copy(savingUser = true, error = null)
            val r = repo.updateUserName(userId, d.user.uid, d.user.email, newName)
            _ui.value = r.fold(
                onSuccess = {
                    st.copy(
                        savingUser = false,
                        details = st.details!!.copy(user = it),
                        savedUser = true,
                        error = null
                    )
                },
                onFailure = { st.copy(savingUser = false, error = it.message) }
            )
        }
    }

    fun savePetName() {
        val st = _ui.value
        val pid = petId ?: run {
            _ui.value = st.copy(error = "No hay mascota asociada")
            return
        }
        val newName = st.petNameInput.trim()
        if (newName.isBlank()) { _ui.value = st.copy(error = "El nombre de la mascota no puede estar vacío"); return }
        viewModelScope.launch {
            _ui.value = st.copy(savingPet = true, error = null)
            val r = repo.updatePetName(pid, userId, newName)
            _ui.value = r.fold(
                onSuccess = {
                    st.copy(
                        savingPet = false,
                        details = st.details!!.copy(pet = it),
                        savedPet = true,
                        error = null
                    )
                },
                onFailure = { st.copy(savingPet = false, error = it.message) }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            prefs.clear()
            _ui.value = _ui.value.copy(doneLogout = true)
        }
    }

    fun deleteProfile() {
        val st = _ui.value
        viewModelScope.launch {
            _ui.value = st.copy(deleting = true, error = null)
            val r = repo.deleteProfile(userId)
            _ui.value = r.fold(
                onSuccess = {
                    prefs.clear()
                    st.copy(deleting = false, doneDeleted = true)
                },
                onFailure = { st.copy(deleting = false, error = it.message) }
            )
        }
    }

    fun consumeSaved() {
        _ui.value = _ui.value.copy(savedUser = false, savedPet = false)
    }
}