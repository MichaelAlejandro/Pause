package com.pause.backend.controllers

import com.pause.backend.models.requests.CreatePauseRequest
import com.pause.backend.models.responses.PauseResponse
import com.pause.backend.routes.Routes
import com.pause.backend.services.PauseService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Routes.BASE_URL + Routes.PAUSES)
class PauseController(
    private val pauseService: PauseService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPause(@RequestBody request: CreatePauseRequest): PauseResponse =
        pauseService.createPause(request)

    @GetMapping
    fun getAll(): List<PauseResponse> = pauseService.getAll()

    @GetMapping(Routes.ID)
    fun getById(@PathVariable id: Long): PauseResponse = pauseService.getById(id)

    @DeleteMapping(Routes.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = pauseService.delete(id)
}
