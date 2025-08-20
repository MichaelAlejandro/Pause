package com.pause.backend.repositories

import com.pause.backend.models.entities.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUid(uid: String): User?

    fun existsByUid(uid: String): Boolean

    fun existsByEmail(email: String): Boolean

    @EntityGraph(attributePaths = ["pet", "pauses", "reviews"])
    fun findWithDetailsById(id: Long): User?
}