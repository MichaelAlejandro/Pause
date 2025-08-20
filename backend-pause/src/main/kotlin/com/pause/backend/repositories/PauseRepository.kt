package com.pause.backend.repositories

import com.pause.backend.models.entities.Pause
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PauseRepository : JpaRepository<Pause, Long> {
    fun findByUserId(userId: Long): List<Pause>
    fun existsByClientEventId(clientEventId: String): Boolean
}
