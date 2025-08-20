package com.pause.backend.repositories

import com.pause.backend.models.entities.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByUserId(userId: Long): List<Review>
    fun existsByClientEventId(clientEventId: String): Boolean
}
