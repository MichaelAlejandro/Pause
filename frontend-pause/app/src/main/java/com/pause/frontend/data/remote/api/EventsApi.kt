package com.pause.frontend.data.remote.api

import com.pause.frontend.data.remote.dto.BatchEventsRequest
import com.pause.frontend.data.remote.dto.BatchEventsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface EventsApi {
    @POST("events/batch")
    suspend fun postBatch(@Body body: BatchEventsRequest): Response<BatchEventsResponse>
}