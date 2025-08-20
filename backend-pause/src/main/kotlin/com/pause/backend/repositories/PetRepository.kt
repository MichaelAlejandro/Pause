package com.pause.backend.repositories

import com.pause.backend.models.entities.Pet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PetRepository : JpaRepository<Pet, Long> {
    fun findByUserId(userId: Long): Pet?
}
