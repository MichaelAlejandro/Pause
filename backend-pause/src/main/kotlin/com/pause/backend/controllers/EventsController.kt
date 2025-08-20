package com.pause.backend.controllers

import com.pause.backend.models.request.BatchEventsRequest
import com.pause.backend.models.responses.BatchEventsResponse
import com.pause.backend.routes.Routes
import com.pause.backend.services.EventIngestService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Routes.BASE_URL + Routes.EVENTS)
class EventsController(
    private val eventIngestService: EventIngestService
) {
    @PostMapping(Routes.BATCH)
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun ingestBatch(@RequestBody request: BatchEventsRequest): BatchEventsResponse =
        eventIngestService.ingestBatch(request)
}