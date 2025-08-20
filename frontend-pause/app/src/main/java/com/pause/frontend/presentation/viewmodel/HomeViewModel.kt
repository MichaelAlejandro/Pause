package com.pause.frontend.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pause.frontend.data.remote.dto.SummaryResponse
import com.pause.frontend.data.repository.SummaryRepository
import com.pause.frontend.data.repository.SummaryRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val loading: Boolean = true,
    val data: SummaryResponse? = null,
    val error: String? = null,
)

class HomeViewModel(
    private val repo: SummaryRepository = SummaryRepositoryImpl()
) : ViewModel() {

    private val _ui = MutableStateFlow(HomeUiState())
    val ui: StateFlow<HomeUiState> = _ui

    fun load(userId: Long) {
        _ui.value = HomeUiState(loading = true)
        viewModelScope.launch {
            val result = repo.getSummary(userId)
            _ui.value = result.fold(
                onSuccess = { HomeUiState(loading = false, data = it) },
                onFailure = { HomeUiState(loading = false, error = it.message ?: "Error desconocido") }
            )
        }
    }

    fun retry(userId: Long) = load(userId)
}