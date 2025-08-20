package com.pause.backend.controllers

import com.pause.backend.models.responses.SummaryResponse
import com.pause.backend.routes.Routes
import com.pause.backend.services.SummaryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Routes.BASE_URL)
class SummaryController(
    private val summaryService: SummaryService
) {
    @GetMapping(Routes.SUMMARY)
    fun getSummary(@RequestParam userId: Long): SummaryResponse =
        summaryService.getSummary(userId)
}
