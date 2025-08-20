package com.pause.frontend.data.repository

import com.pause.frontend.data.remote.api.Services
import com.pause.frontend.data.remote.dto.CreateReviewRequest
import com.pause.frontend.data.remote.dto.ReviewResponse

class ReviewsRepositoryImpl : ReviewsRepository {
    override suspend fun createReview(body: CreateReviewRequest): Result<ReviewResponse> {
        return try {
            val res = Services.reviews.createReview(body)
            if (res.isSuccessful && res.body() != null) Result.success(res.body()!!)
            else Result.failure(IllegalStateException("HTTP ${res.code()}: ${res.errorBody()?.string()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}