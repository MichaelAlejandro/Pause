package com.pause.frontend.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pause.frontend.data.remote.dto.CreatePauseRequest
import com.pause.frontend.data.repository.PausesRepository
import com.pause.frontend.data.repository.PausesRepositoryImpl
import com.pause.frontend.utils.TimeUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PauseUiState(
    val running: Boolean = false,
    val secondsLeft: Int = 60,           // demo: 1 minuto
    val done: Boolean = false,
    val error: String? = null
)

class PauseViewModel(
    private val repo: PausesRepository = PausesRepositoryImpl()
) : ViewModel() {

    private val _ui = MutableStateFlow(PauseUiState())
    val ui: StateFlow<PauseUiState> = _ui

    private var job: Job? = null

    fun start() {
        if (_ui.value.running) return
        _ui.value = _ui.value.copy(running = true, error = null)
        job = viewModelScope.launch {
            var s = _ui.value.secondsLeft
            while (s > 0) {
                delay(1000)
                s -= 1
                _ui.value = _ui.value.copy(secondsLeft = s)
            }
        }
    }

    fun cancel() {
        job?.cancel()
        _ui.value = PauseUiState()
    }

    fun finishAndSend(userId: Long) {
        job?.cancel()
        viewModelScope.launch {
            // En demo registramos 1 minuto siempre
            val req = CreatePauseRequest(
                userId = userId,
                durationMinutes = 1,
                type = "active",
                source = "manual",
                clientEventId = TimeUtils.newEventId("evt-pause"),
                timestamp = TimeUtils.nowIso()
            )
            val r = repo.createPause(req)
            _ui.value = r.fold(
                onSuccess = { PauseUiState(done = true) },
                onFailure = { PauseUiState(error = it.message) }
            )
        }
    }
}