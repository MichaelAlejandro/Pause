package com.pause.frontend.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pause.frontend.data.remote.dto.CreateReviewRequest
import com.pause.frontend.data.repository.ReviewsRepository
import com.pause.frontend.data.repository.ReviewsRepositoryImpl
import com.pause.frontend.utils.TimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ReviewQuestion(
    val id: String,
    val statement: String,
    val options: List<String>,
    val correctIndex: Int,
    val topic: String
)

data class ReviewUiState(
    val q: ReviewQuestion? = null,
    val selected: Int? = null,
    val sending: Boolean = false,
    val done: Boolean = false,
    val error: String? = null
)

class ReviewViewModel(
    private val repo: ReviewsRepository = ReviewsRepositoryImpl()
) : ViewModel() {

    private val _ui = MutableStateFlow(ReviewUiState())
    val ui: StateFlow<ReviewUiState> = _ui

    // --- Banco de preguntas (añade/edita libremente) ---
    private val questionBank: List<ReviewQuestion> = listOf(
        ReviewQuestion(
            id = "q001",
            statement = "¿Qué es MVVM?",
            options = listOf("Patrón de arquitectura", "Librería de UI", "Framework de red", "Motor de base de datos"),
            correctIndex = 0,
            topic = "android"
        ),
        ReviewQuestion(
            id = "q002",
            statement = "En Kotlin, ¿cuál palabra clave crea una clase inmutable por defecto?",
            options = listOf("class", "data class", "object", "sealed"),
            correctIndex = 2, // object crea singleton, pero si lo que quieres es 'data class', cambia a 1
            topic = "kotlin"
        ),
        ReviewQuestion(
            id = "q003",
            statement = "¿Qué hace 'suspend' en una función?",
            options = listOf("La ejecuta en otro proceso", "Permite pausar/reanudar en corrutinas", "La vuelve síncrona", "La compila más rápido"),
            correctIndex = 1,
            topic = "kotlin/coroutines"
        ),
        ReviewQuestion(
            id = "q004",
            statement = "¿Qué capa NO pertenece a MVVM clásico?",
            options = listOf("Model", "View", "ViewController", "ViewModel"),
            correctIndex = 2,
            topic = "arquitectura"
        ),
        ReviewQuestion(
            id = "q005",
            statement = "Con Retrofit + Kotlinx Serialization, ¿qué anotación se usa en DTOs?",
            options = listOf("@SerializedName", "@Json", "@SerialName", "@JsonName"),
            correctIndex = 2,
            topic = "network"
        ),
        ReviewQuestion(
            id = "q006",
            statement = "¿Qué hace collectAsState() en Compose?",
            options = listOf("Convierte un Flow a estado Compose", "Convierte LiveData en StateFlow", "Ejecuta un remember", "Lanza una corrutina en IO"),
            correctIndex = 0,
            topic = "compose"
        ),
        ReviewQuestion(
            id = "q007",
            statement = "¿Cuál es una buena práctica para capas de datos?",
            options = listOf("Repositorios", "Actividades estáticas", "Servicios singleton globales sin DI", "Usar siempre hilos manuales"),
            correctIndex = 0,
            topic = "arquitectura"
        ),
        ReviewQuestion(
            id = "q008",
            statement = "En Compose, ¿qué hace remember?",
            options = listOf("Guarda estado entre recomposiciones", "Evita recomposición siempre", "Bloquea el hilo UI", "Crea un ViewModel"),
            correctIndex = 0,
            topic = "compose"
        )
    )

    private var bag: MutableList<ReviewQuestion> = questionBank.shuffled().toMutableList()

    init {
        nextQuestion()
    }

    private fun nextQuestion() {
        if (bag.isEmpty()) bag = questionBank.shuffled().toMutableList()
        val q = bag.removeAt(bag.lastIndex)
        _ui.value = ReviewUiState(q = q)
    }

    fun shuffleQuestion() {
        if (_ui.value.sending) return
        nextQuestion()
    }

    fun select(i: Int) {
        _ui.value = _ui.value.copy(selected = i, error = null)
    }

    fun submit(userId: Long) {
        val st = _ui.value
        val q = st.q ?: return
        val sel = st.selected ?: run {
            _ui.value = st.copy(error = "Selecciona una opción")
            return
        }
        val correct = sel == q.correctIndex

        viewModelScope.launch {
            _ui.value = st.copy(sending = true, error = null)
            val req = CreateReviewRequest(
                userId = userId,
                question = q.statement,
                userAnswer = q.options[sel],
                correct = correct,
                questionId = q.id,
                topic = q.topic,
                clientEventId = TimeUtils.newEventId("evt-review"),
                timestamp = TimeUtils.nowIso()
            )
            val r = repo.createReview(req)
            _ui.value = r.fold(
                onSuccess = { st.copy(sending = false, done = true) },
                onFailure = { st.copy(sending = false, error = it.message ?: "Error") }
            )
        }
    }
}
