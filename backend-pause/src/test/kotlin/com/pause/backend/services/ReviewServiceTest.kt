package com.pause.backend.services

import com.pause.backend.exceptions.exceptions.DuplicateResourceException
import com.pause.backend.exceptions.exceptions.InvalidRequestException
import com.pause.backend.exceptions.exceptions.ResourceNotFoundException
import com.pause.backend.models.entities.Review
import com.pause.backend.models.entities.User
import com.pause.backend.models.requests.CreateReviewRequest
import com.pause.backend.repositories.ReviewRepository
import com.pause.backend.repositories.UserRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.LocalDateTime
import java.util.*

class ReviewServiceTest {

    private lateinit var reviewRepository: ReviewRepository
    private lateinit var userRepository: UserRepository
    private lateinit var reviewService: ReviewService

    @BeforeEach
    fun setUp() {
        reviewRepository = mock(ReviewRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        reviewService = ReviewService(reviewRepository, userRepository)
    }

    @Test
    fun should_create_a_review() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val request = CreateReviewRequest(
            userId = 1L,
            question = "¿Qué es Kotlin?",
            userAnswer = "Lenguaje",
            correct = true,
            questionId = "q-1",
            topic = "android",
            clientEventId = "evt-2",
            timestamp = LocalDateTime.now()
        )
        val review = Review(
            user = user,
            question = request.question,
            userAnswer = request.userAnswer,
            correct = request.correct,
            questionId = request.questionId,
            topic = request.topic,
            clientEventId = request.clientEventId,
            timestamp = request.timestamp
        )

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
        `when`(reviewRepository.existsByClientEventId("evt-2")).thenReturn(false)
        `when`(reviewRepository.save(any(Review::class.java))).thenReturn(review)

        val result = reviewService.createReview(request)

        assertEquals("¿Qué es Kotlin?", result.question)
        assertTrue(result.correct)
        assertEquals("q-1", result.questionId)
        assertEquals("android", result.topic)
        assertEquals("evt-2", result.clientEventId)
    }

    @Test
    fun should_throw_when_question_blank() {
        val request = CreateReviewRequest(
            userId = 1L, question = "", userAnswer = "A", correct = false, timestamp = LocalDateTime.now()
        )
        assertThrows<InvalidRequestException> { reviewService.createReview(request) }
    }

    @Test
    fun should_throw_duplicate_when_clientEventId_exists() {
        val request = CreateReviewRequest(
            userId = 1L, question = "Q", userAnswer = "A", correct = false, clientEventId = "dup", timestamp = LocalDateTime.now()
        )
        `when`(reviewRepository.existsByClientEventId("dup")).thenReturn(true)
        assertThrows<DuplicateResourceException> { reviewService.createReview(request) }
        verify(reviewRepository, never()).save(any(Review::class.java))
    }

    @Test
    fun should_throw_exception_when_user_not_found_on_create() {
        val request = CreateReviewRequest(
            userId = 1L, question = "Q", userAnswer = "A", correct = false, timestamp = LocalDateTime.now()
        )
        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { reviewService.createReview(request) }
    }

    @Test
    fun should_return_all_reviews() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val review = Review(user = user, question = "Q", userAnswer = "A", correct = true, timestamp = LocalDateTime.now())
        `when`(reviewRepository.findAll()).thenReturn(listOf(review))

        val result = reviewService.getAll()
        assertEquals(1, result.size)
        assertEquals("Q", result[0].question)
    }

    @Test
    fun should_return_review_by_id() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val review = Review(user = user, question = "Pregunta", userAnswer = "Respuesta", correct = true, timestamp = LocalDateTime.now())
        `when`(reviewRepository.findById(1L)).thenReturn(Optional.of(review))

        val result = reviewService.getById(1L)
        assertEquals("Pregunta", result.question)
    }

    @Test
    fun should_throw_exception_when_review_not_found_by_id() {
        `when`(reviewRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { reviewService.getById(1L) }
    }

    @Test
    fun should_delete_review() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val review = Review(user = user, question = "P", userAnswer = "R", correct = false, timestamp = LocalDateTime.now())
        `when`(reviewRepository.findById(1L)).thenReturn(Optional.of(review))

        reviewService.delete(1L)
        verify(reviewRepository).delete(review)
    }

    @Test
    fun should_throw_exception_when_deleting_non_existent_review() {
        `when`(reviewRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { reviewService.delete(1L) }
    }
}