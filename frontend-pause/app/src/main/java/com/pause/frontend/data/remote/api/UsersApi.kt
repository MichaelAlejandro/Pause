package com.pause.frontend.data.remote.api

import com.pause.frontend.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface UsersApi {

    @POST("users")
    suspend fun createUser(@Body body: CreateUserRequest): Response<UserResponse>

    @GET("users")
    suspend fun listUsers(): Response<List<UserResponse>>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Long): Response<UserResponse>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body body: UpdateUserRequest
    ): Response<UserResponse>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Unit>

    @GET("users/{id}/details")
    suspend fun getUserDetails(@Path("id") id: Long): Response<UserDetailResponse>
}