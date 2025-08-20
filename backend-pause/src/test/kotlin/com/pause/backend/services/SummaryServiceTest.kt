package com.pause.backend.services

import com.pause.backend.exceptions.exceptions.ResourceNotFoundException
import com.pause.backend.models.entities.Pause
import com.pause.backend.models.entities.Pet
import com.pause.backend.models.entities.Review
import com.pause.backend.models.entities.User
import com.pause.backend.repositories.PauseRepository
import com.pause.backend.repositories.PetRepository
import com.pause.backend.repositories.ReviewRepository
import com.pause.backend.repositories.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.LocalDateTime
import java.util.*

class SummaryServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var petRepository: PetRepository
    private lateinit var pauseRepository: PauseRepository
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var summaryService: SummaryService

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        petRepository = mock(PetRepository::class.java)
        pauseRepository = mock(PauseRepository::class.java)
        reviewRepository = mock(ReviewRepository::class.java)
        summaryService = SummaryService(userRepository, petRepository, pauseRepository, reviewRepository)
    }

    @Test
    fun should_build_summary_for_existing_user_and_pet() {
        val userId = 7L
        val user = User(uid = "uid7", userName = "M", email = "m@e.c")
        val now = LocalDateTime.now()

        val pet = Pet(user = user, petName = "Nano")
        pet.stateLevel = 4
        pet.score = 123
        pet.equippedItems = mutableMapOf("head" to "hat_red")
        pet.unlockedItems = mutableSetOf("hat")
        pet.lastUpdatedAt = now

        val pause = Pause(user = user, durationMinutes = 5, timestamp = now)
        val review = Review(user = user, question = "Q", userAnswer = "A", correct = true, timestamp = now)

        `when`(userRepository.findById(userId)).thenReturn(Optional.of(user))
        `when`(petRepository.findByUserId(userId)).thenReturn(pet)
        `when`(pauseRepository.findByUserId(userId)).thenReturn(listOf(pause))
        `when`(reviewRepository.findByUserId(userId)).thenReturn(listOf(review, review))

        val summary = summaryService.getSummary(userId)

        assertEquals(userId, summary.userId)
        assertEquals(4, summary.stateLevel)
        assertEquals(123, summary.score)
        assertEquals(1, summary.recentPausesCount)
        assertEquals(2, summary.recentReviewsCount)
        assertEquals(setOf("hat"), summary.unlockedItems)
        assertEquals(mapOf("head" to "hat_red"), summary.equippedItems)
        assertEquals(now, summary.lastUpdatedAt)
        assertTrue(summary.nextUnlocks.isNotEmpty()) // la impl añade al menos un preview
    }

    @Test
    fun should_throw_when_user_not_found() {
        val userId = 99L
        `when`(userRepository.findById(userId)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { summaryService.getSummary(userId) }
    }

    @Test
    fun should_throw_when_pet_not_found() {
        val userId = 3L
        val user = User(uid = "u3", userName = "Name", email = "e@e.c")
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(user))
        `when`(petRepository.findByUserId(userId)).thenReturn(null)
        assertThrows<ResourceNotFoundException> { summaryService.getSummary(userId) }
    }
}