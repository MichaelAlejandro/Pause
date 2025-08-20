package com.pause.backend.mappers

import com.pause.backend.models.entities.Pause
import com.pause.backend.models.responses.PauseResponse

fun Pause.toResponse(): PauseResponse =
    PauseResponse(
        id = requireNotNull(this.id),
        userId = requireNotNull(this.user.id),
        durationMinutes = this.durationMinutes,
        type = this.type,
        source = this.source,
        clientEventId = this.clientEventId,
        timestamp = this.timestamp
    )
