package com.pause.frontend.data.remote.api

import com.pause.frontend.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface PetsApi {

    @POST("pets")
    suspend fun createPet(@Body body: CreatePetRequest): Response<PetResponse>

    @GET("pets")
    suspend fun listPets(): Response<List<PetResponse>>

    @GET("pets/{id}")
    suspend fun getPet(@Path("id") id: Long): Response<PetResponse>

    @PATCH("pets/{id}/name")
    suspend fun renamePet(
        @Path("id") id: Long,
        @Body body: PatchPetNameRequest
    ): Response<PetResponse>

    @PATCH("pets/{id}/customization")
    suspend fun patchCustomization(
        @Path("id") id: Long,
        @Body body: PatchCustomizationRequest
    ): Response<PetResponse>

    @PUT("pets/{id}/equip")
    suspend fun equipItems(
        @Path("id") id: Long,
        @Body body: EquipItemsRequest
    ): Response<PetResponse>

    @DELETE("pets/{id}")
    suspend fun deletePet(@Path("id") id: Long): Response<Unit>
}