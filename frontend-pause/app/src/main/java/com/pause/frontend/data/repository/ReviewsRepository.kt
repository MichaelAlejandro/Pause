package com.pause.frontend.data.repository

import com.pause.frontend.data.remote.dto.CreateReviewRequest
import com.pause.frontend.data.remote.dto.ReviewResponse

interface ReviewsRepository {
    suspend fun createReview(body: CreateReviewRequest): Result<ReviewResponse>
}