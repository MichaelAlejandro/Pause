package com.pause.backend.services

import com.pause.backend.exceptions.exceptions.DuplicateResourceException
import com.pause.backend.exceptions.exceptions.InvalidRequestException
import com.pause.backend.exceptions.exceptions.ResourceNotFoundException
import com.pause.backend.mappers.toResponse
import com.pause.backend.models.entities.Pause
import com.pause.backend.models.requests.CreatePauseRequest
import com.pause.backend.models.responses.PauseResponse
import com.pause.backend.repositories.PauseRepository
import com.pause.backend.repositories.UserRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
class PauseService(
    private val pauseRepository: PauseRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createPause(request: CreatePauseRequest): PauseResponse {
        if (request.durationMinutes <= 0) {
            throw InvalidRequestException(
                "durationMinutes must be > 0",
                mapOf("durationMinutes" to "must be > 0")
            )
        }

        // Idempotencia opcional
        request.clientEventId?.let { cid ->
            if (cid.isNotBlank() && pauseRepository.existsByClientEventId(cid)) {
                throw DuplicateResourceException("Pause already exists for clientEventId=$cid")
            }
        }

        val user = userRepository.findById(request.userId).orElseThrow {
            ResourceNotFoundException("User not found (id=${request.userId})")
        }

        val pause = Pause(
            user = user,
            durationMinutes = request.durationMinutes,
            type = request.type,
            source = request.source,
            clientEventId = request.clientEventId,
            timestamp = request.timestamp
        )
        return pauseRepository.save(pause).toResponse()
    }

    fun getAll(): List<PauseResponse> = pauseRepository.findAll().map { it.toResponse() }

    fun getById(id: Long): PauseResponse =
        pauseRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Pause with ID $id not found")
        }.toResponse()

    @Transactional
    fun delete(id: Long) {
        val pause = pauseRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Pause with ID $id not found")
        }
        pauseRepository.delete(pause)
    }
}