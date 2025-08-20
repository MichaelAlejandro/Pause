package com.pause.backend.models.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "reviews",
    indexes = [
        Index(name = "idx_reviews_user_ts", columnList = "user_id,timestamp"),
        Index(name = "idx_reviews_client_event", columnList = "client_event_id")
    ]
)
class Review(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Lob
    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    var question: String,

    @Column(name = "user_answer", nullable = false)
    var userAnswer: String,

    @Column(name = "correct", nullable = false)
    var correct: Boolean,

    @Column(name = "question_id", length = 64)
    var questionId: String? = null,

    @Column(name = "topic", length = 64)
    var topic: String? = null,

    @Column(name = "client_event_id", length = 64)
    var clientEventId: String? = null,

    @Column(name = "timestamp", nullable = false)
    var timestamp: LocalDateTime = LocalDateTime.now()
) : BaseEntity()