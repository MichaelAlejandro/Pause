package com.pause.backend.models.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "pauses",
    indexes = [
        Index(name = "idx_pauses_user_ts", columnList = "user_id,timestamp"),
        Index(name = "idx_pauses_client_event", columnList = "client_event_id")
    ]
)
class Pause(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "duration_minutes", nullable = false)
    var durationMinutes: Int,

    @Column(name = "type", length = 32)
    var type: String? = null,

    @Column(name = "source", length = 32)
    var source: String? = null,

    @Column(name = "client_event_id", length = 64)
    var clientEventId: String? = null,

    @Column(name = "timestamp", nullable = false)
    var timestamp: LocalDateTime = LocalDateTime.now()
) : BaseEntity()