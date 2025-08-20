package com.pause.frontend.data.remote.api

import com.pause.frontend.data.remote.ApiClient

object Services {
    val users: UsersApi by lazy { ApiClient.retrofit.create(UsersApi::class.java) }
    val pets: PetsApi by lazy { ApiClient.retrofit.create(PetsApi::class.java) }
    val pauses: PausesApi by lazy { ApiClient.retrofit.create(PausesApi::class.java) }
    val reviews: ReviewsApi by lazy { ApiClient.retrofit.create(ReviewsApi::class.java) }
    val summary: SummaryApi by lazy { ApiClient.retrofit.create(SummaryApi::class.java) }
    val events: EventsApi by lazy { ApiClient.retrofit.create(EventsApi::class.java) }
}