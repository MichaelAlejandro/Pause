package com.pause.backend.controllers

import com.pause.backend.models.requests.CreateReviewRequest
import com.pause.backend.models.responses.ReviewResponse
import com.pause.backend.routes.Routes
import com.pause.backend.services.ReviewService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Routes.BASE_URL + Routes.REVIEWS)
class ReviewController(
    private val reviewService: ReviewService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createReview(@RequestBody request: CreateReviewRequest): ReviewResponse =
        reviewService.createReview(request)

    @GetMapping
    fun getAllReviews(): List<ReviewResponse> = reviewService.getAll()

    @GetMapping(Routes.ID)
    fun getReviewById(@PathVariable id: Long): ReviewResponse = reviewService.getById(id)

    @DeleteMapping(Routes.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteReview(@PathVariable id: Long) = reviewService.delete(id)
}
