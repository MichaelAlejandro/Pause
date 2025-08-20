package com.pause.backend.services

import com.pause.backend.exceptions.exceptions.DuplicateResourceException
import com.pause.backend.exceptions.exceptions.InvalidRequestException
import com.pause.backend.exceptions.exceptions.ResourceNotFoundException
import com.pause.backend.mappers.toResponse
import com.pause.backend.models.entities.Review
import com.pause.backend.models.requests.CreateReviewRequest
import com.pause.backend.models.responses.ReviewResponse
import com.pause.backend.repositories.ReviewRepository
import com.pause.backend.repositories.UserRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createReview(request: CreateReviewRequest): ReviewResponse {
        if (request.question.isBlank()) {
            throw InvalidRequestException("question must not be blank", mapOf("question" to "required"))
        }

        // Idempotencia opcional
        request.clientEventId?.let { cid ->
            if (cid.isNotBlank() && reviewRepository.existsByClientEventId(cid)) {
                throw DuplicateResourceException("Review already exists for clientEventId=$cid")
            }
        }

        val user = userRepository.findById(request.userId).orElseThrow {
            ResourceNotFoundException("User not found (id=${request.userId})")
        }

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
        return reviewRepository.save(review).toResponse()
    }

    fun getAll(): List<ReviewResponse> = reviewRepository.findAll().map { it.toResponse() }

    fun getById(id: Long): ReviewResponse =
        reviewRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Review with ID $id not found")
        }.toResponse()

    @Transactional
    fun delete(id: Long) {
        val review = reviewRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Review with ID $id not found")
        }
        reviewRepository.delete(review)
    }
}