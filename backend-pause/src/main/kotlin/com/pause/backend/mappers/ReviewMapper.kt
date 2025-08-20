package com.pause.backend.mappers

import com.pause.backend.models.entities.Review
import com.pause.backend.models.responses.ReviewResponse

fun Review.toResponse(): ReviewResponse =
    ReviewResponse(
        id = requireNotNull(this.id),
        userId = requireNotNull(this.user.id),
        question = this.question,
        userAnswer = this.userAnswer,
        correct = this.correct,
        questionId = this.questionId,
        topic = this.topic,
        clientEventId = this.clientEventId,
        timestamp = this.timestamp
    )
