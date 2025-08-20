package com.pause.frontend.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pause.frontend.data.remote.dto.SummaryResponse
import com.pause.frontend.data.repository.WardrobeRepository
import com.pause.frontend.data.repository.WardrobeRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class WardrobeUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val summary: SummaryResponse? = null,
    val selectedHead: String? = null,
    val selectedEyes: String? = null,
    val saving: Boolean = false,
    val saved: Boolean = false,
    val hasChanges: Boolean = false
)

class WardrobeViewModel(
    private val repo: WardrobeRepository = WardrobeRepositoryImpl()
) : ViewModel() {

    private var userId: Long = -1
    private var petId: Long = -1

    // guardamos el estado inicial para saber si hay cambios
    private var initialHead: String? = null
    private var initialEyes: String? = null

    private val _ui = MutableStateFlow(WardrobeUiState())
    val ui: StateFlow<WardrobeUiState> = _ui

    fun load(userId: Long, petId: Long) {
        this.userId = userId
        this.petId = petId
        _ui.value = WardrobeUiState(loading = true)
        viewModelScope.launch {
            val r = repo.loadSummary(userId)
            _ui.value = r.fold(
                onSuccess = { s ->
                    initialHead = s.equippedItems?.get("head")
                    initialEyes = s.equippedItems?.get("eyes")
                    WardrobeUiState(
                        loading = false,
                        summary = s,
                        selectedHead = initialHead,
                        selectedEyes = initialEyes,
                        hasChanges = false
                    )
                },
                onFailure = { WardrobeUiState(loading = false, error = it.message) }
            )
        }
    }

    fun select(slot: String, itemId: String?) {
        val st = _ui.value
        val newState = when (slot) {
            "head" -> st.copy(selectedHead = itemId)
            "eyes" -> st.copy(selectedEyes = itemId)
            else -> st
        }
        _ui.value = newState.copy(
            hasChanges = (newState.selectedHead != initialHead) || (newState.selectedEyes != initialEyes),
            saved = false
        )
    }

    fun save() {
        val st = _ui.value
        val sum = st.summary ?: return

        // Solo enviamos slots que CAMBIARON. Para des-equipar mandamos "".
        val payload = mutableMapOf<String, String>()
        if (st.selectedHead != initialHead) payload["head"] = st.selectedHead ?: ""
        if (st.selectedEyes != initialEyes) payload["eyes"] = st.selectedEyes ?: ""

        if (payload.isEmpty()) {
            _ui.value = st.copy(saved = true, hasChanges = false) // nada que enviar
            return
        }

        viewModelScope.launch {
            _ui.value = st.copy(saving = true, error = null)
            val r = repo.equip(petId = petId, userId = userId, equipped = payload)
            _ui.value = r.fold(
                onSuccess = {
                    // actualizamos “iniciales” con lo que quedó seleccionado
                    initialHead = st.selectedHead
                    initialEyes = st.selectedEyes
                    st.copy(saving = false, saved = true, hasChanges = false)
                },
                onFailure = { st.copy(saving = false, error = it.message ?: "Error") }
            )
        }
    }
}