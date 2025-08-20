package com.pause.frontend.data.remote.api

import com.pause.frontend.data.remote.dto.CreateReviewRequest
import com.pause.frontend.data.remote.dto.ReviewResponse
import retrofit2.Response
import retrofit2.http.*

interface ReviewsApi {

    @POST("reviews")
    suspend fun createReview(@Body body: CreateReviewRequest): Response<ReviewResponse>

    @GET("reviews")
    suspend fun listReviews(): Response<List<ReviewResponse>>

    @GET("reviews/{id}")
    suspend fun getReview(@Path("id") id: Long): Response<ReviewResponse>

    @DELETE("reviews/{id}")
    suspend fun deleteReview(@Path("id") id: Long): Response<Unit>
}