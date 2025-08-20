package com.pause.backend.services

import com.pause.backend.exceptions.exceptions.ResourceNotFoundException
import com.pause.backend.models.responses.SummaryResponse
import com.pause.backend.models.responses.UnlockPreview
import com.pause.backend.repositories.PauseRepository
import com.pause.backend.repositories.PetRepository
import com.pause.backend.repositories.ReviewRepository
import com.pause.backend.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.math.min

@Service
class SummaryService(
    private val userRepository: UserRepository,
    private val petRepository: PetRepository,
    private val pauseRepository: PauseRepository,
    private val reviewRepository: ReviewRepository
) {

    private enum class ReqType { MINUTES, REVIEWS }

    private data class SubReq(val type: ReqType, val required: Int)
    private data class UnlockRule(
        val itemId: String,
        val requirementText: String,
        val subReqs: List<SubReq>
    )

    // ---- Reglas para los 4 objetos (ajusta a tu gusto) ----
    private val rules: List<UnlockRule> = listOf(
        UnlockRule(
            itemId = "hat_red",
            requirementText = "1 repaso y 1 minuto de pausa",
            subReqs = listOf(SubReq(ReqType.REVIEWS, 1), SubReq(ReqType.MINUTES, 1))
        ),
        UnlockRule(
            itemId = "hat_blue",
            requirementText = "2 repasos y 1 minutos de pausa",
            subReqs = listOf(SubReq(ReqType.REVIEWS, 2), SubReq(ReqType.MINUTES, 1))
        ),
        UnlockRule(
            itemId = "sunglasses",
            requirementText = "3 repasos y 1 minutos de pausa",
            subReqs = listOf(SubReq(ReqType.REVIEWS, 3), SubReq(ReqType.MINUTES, 1))
        ),
        UnlockRule(
            itemId = "crown",
            requirementText = "4 repasos y 1 minutos de pausa",
            subReqs = listOf(SubReq(ReqType.REVIEWS, 4), SubReq(ReqType.MINUTES, 1))
        )
    )

    @Transactional // NO readOnly porque podemos actualizar el pet
    fun getSummary(userId: Long): SummaryResponse {
        val user = userRepository.findById(userId).orElseThrow {
            ResourceNotFoundException("User with ID $userId not found")
        }

        val pet = petRepository.findByUserId(userId)
            ?: throw ResourceNotFoundException("Pet for user $userId not found")

        // 1) Métricas base
        val pauses = pauseRepository.findByUserId(userId)
        val totalPauseMinutes = pauses.sumOf { it.durationMinutes ?: 0 }
        val reviews = reviewRepository.findByUserId(userId)
        val totalReviews = reviews.size

        // 2) Score / StateLevel (simple; ajústalo si quieres otra curva)
        val newScore = totalPauseMinutes * 1 + totalReviews * 2
        val newState = when {
            newScore <= 0 -> 1
            newScore < 3  -> 2
            newScore < 6  -> 3
            newScore < 10 -> 4
            else          -> 5
        }

        // 3) Desbloqueos y próximos desbloqueos
        val unlocked: MutableSet<String> = pet.unlockedItems.toMutableSet()
        val nextUnlocks = mutableListOf<UnlockPreview>()

        rules.forEach { rule ->
            if (rule.itemId !in unlocked) {
                val (progress, required) = progressFor(rule, totalReviews, totalPauseMinutes)
                if (progress >= required) {
                    unlocked.add(rule.itemId)
                } else {
                    nextUnlocks += UnlockPreview(
                        itemId = rule.itemId,
                        requirementText = rule.requirementText,
                        progress = progress,
                        required = required
                    )
                }
            }
        }

        // 4) Persistir cambios si hubo alguno
        val changed = (pet.score != newScore) ||
                (pet.stateLevel != newState) ||
                (unlocked != pet.unlockedItems)

        if (changed) {
            pet.score = newScore
            pet.stateLevel = newState
            pet.unlockedItems = unlocked
            pet.lastUpdatedAt = LocalDateTime.now()
            petRepository.save(pet)
        }

        val visibleUserName = user.userName

        // 5) Respuesta
        return SummaryResponse(
            userId = userId,
            stateLevel = pet.stateLevel,
            score = pet.score,
            recentPausesCount = pauses.size,     // si prefieres últimos 7 días, aquí filtra por fecha
            recentReviewsCount = totalReviews,
            unlockedItems = pet.unlockedItems,   // si tu DTO pide List<String>: .toList()
            equippedItems = pet.equippedItems,
            nextUnlocks = nextUnlocks,
            lastUpdatedAt = pet.lastUpdatedAt,
            userName = visibleUserName,
            petName = pet.petName
        )
    }

    private fun progressFor(
        rule: UnlockRule,
        totalReviews: Int,
        totalPauseMinutes: Int
    ): Pair<Int, Int> {
        var progress = 0
        var required = 0
        rule.subReqs.forEach { sr ->
            required += sr.required
            val have = when (sr.type) {
                ReqType.REVIEWS -> totalReviews
                ReqType.MINUTES -> totalPauseMinutes
            }
            progress += min(have, sr.required)
        }
        return progress to required
    }
}