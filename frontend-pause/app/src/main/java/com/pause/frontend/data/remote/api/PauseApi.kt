package com.pause.frontend.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PauseApi {

    // -------- Users --------
    @POST("users")
    suspend fun createUser(@Body body: Map<String, Any?>): Response<ResponseBody>

    @GET("users")
    suspend fun listUsers(): Response<ResponseBody>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Long): Response<ResponseBody>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body body: Map<String, Any?>): Response<ResponseBody>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Unit>

    @GET("users/{id}/details")
    suspend fun getUserDetails(@Path("id") id: Long): Response<ResponseBody>

    // -------- Pets --------
    @POST("pets")
    suspend fun createPet(@Body body: Map<String, Any?>): Response<ResponseBody>

    @GET("pets")
    suspend fun listPets(): Response<ResponseBody>

    @GET("pets/{id}")
    suspend fun getPet(@Path("id") id: Long): Response<ResponseBody>

    @PATCH("pets/{id}/name")
    suspend fun renamePet(@Path("id") id: Long, @Body body: Map<String, Any?>): Response<ResponseBody>

    @PATCH("pets/{id}/customization")
    suspend fun customizePet(@Path("id") id: Long, @Body body: Map<String, Any?>): Response<ResponseBody>

    @PUT("pets/{id}/equip")
    suspend fun equipItems(@Path("id") id: Long, @Body body: Map<String, Any?>): Response<ResponseBody>

    @DELETE("pets/{id}")
    suspend fun deletePet(@Path("id") id: Long): Response<Unit>

    // -------- Pauses --------
    @POST("pauses")
    suspend fun createPause(@Body body: Map<String, Any?>): Response<ResponseBody>

    @GET("pauses")
    suspend fun listPauses(): Response<ResponseBody>

    @GET("pauses/{id}")
    suspend fun getPause(@Path("id") id: Long): Response<ResponseBody>

    @DELETE("pauses/{id}")
    suspend fun deletePause(@Path("id") id: Long): Response<Unit>

    // -------- Reviews --------
    @POST("reviews")
    suspend fun createReview(@Body body: Map<String, Any?>): Response<ResponseBody>

    @GET("reviews")
    suspend fun listReviews(): Response<ResponseBody>

    @GET("reviews/{id}")
    suspend fun getReview(@Path("id") id: Long): Response<ResponseBody>

    @DELETE("reviews/{id}")
    suspend fun deleteReview(@Path("id") id: Long): Response<Unit>

    // -------- Summary (Home) --------
    @GET("summary")
    suspend fun getSummary(@Query("userId") userId: Long): Response<ResponseBody>

    // -------- Batch events --------
    @POST("events/batch")
    suspend fun postEventsBatch(@Body body: Map<String, Any?>): Response<ResponseBody>
}