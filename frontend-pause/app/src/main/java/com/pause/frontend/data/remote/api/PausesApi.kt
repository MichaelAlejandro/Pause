package com.pause.frontend.data.remote.api

import com.pause.frontend.data.remote.dto.CreatePauseRequest
import com.pause.frontend.data.remote.dto.PauseResponse
import retrofit2.Response
import retrofit2.http.*

interface PausesApi {

    @POST("pauses")
    suspend fun createPause(@Body body: CreatePauseRequest): Response<PauseResponse>

    @GET("pauses")
    suspend fun listPauses(): Response<List<PauseResponse>>

    @GET("pauses/{id}")
    suspend fun getPause(@Path("id") id: Long): Response<PauseResponse>

    @DELETE("pauses/{id}")
    suspend fun deletePause(@Path("id") id: Long): Response<Unit>
}