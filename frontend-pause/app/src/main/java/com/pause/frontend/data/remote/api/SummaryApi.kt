package com.pause.frontend.data.remote.api

import com.pause.frontend.data.remote.dto.SummaryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SummaryApi {
    @GET("summary")
    suspend fun getSummary(@Query("userId") userId: Long): Response<SummaryResponse>
}