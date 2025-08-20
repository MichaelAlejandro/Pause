package com.pause.backend.services

import com.pause.backend.exceptions.exceptions.DuplicateResourceException
import com.pause.backend.exceptions.exceptions.ResourceNotFoundException
import com.pause.backend.mappers.toDetailResponse
import com.pause.backend.mappers.toResponse
import com.pause.backend.models.requests.CreateUserRequest
import com.pause.backend.models.responses.UserDetailResponse
import com.pause.backend.models.responses.UserResponse
import com.pause.backend.repositories.PauseRepository
import com.pause.backend.repositories.PetRepository
import com.pause.backend.repositories.ReviewRepository
import com.pause.backend.repositories.UserRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val petRepository: PetRepository,
    private val pauseRepository: PauseRepository,
    private val reviewRepository: ReviewRepository
) {

    @Transactional
    fun createUser(request: CreateUserRequest): UserResponse {
        val user = request.toEntity()
        if (userRepository.findByUid(request.uid) != null) {
            throw DuplicateResourceException("User with uid=${request.uid} already exists")
        }
        if (userRepository.existsByEmail(request.email)) {
            throw DuplicateResourceException("User with email=${request.email} already exists")
        }
        return userRepository.save(user).toResponse()
    }

    fun getAllUsers(): List<UserResponse> =
        userRepository.findAll().map { it.toResponse() }

    fun getUserById(id: Long): UserResponse =
        userRepository.findById(id).orElseThrow {
            ResourceNotFoundException("User with ID $id not found")
        }.toResponse()

    @Transactional
    fun updateUser(id: Long, request: CreateUserRequest): UserResponse {
        val user = userRepository.findById(id).orElseThrow {
            ResourceNotFoundException("User with ID $id not found")
        }
        user.userName = request.userName
        user.email = request.email
        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun deleteUser(id: Long) {
        val user = userRepository.findById(id).orElseThrow {
            ResourceNotFoundException("User with ID $id not found")
        }
        userRepository.delete(user)
    }

    /** Opcional: endpoint /users/{id}/details devolviendo UserDetailResponse */
    @Transactional(readOnly = true)
    fun getUserDetails(id: Long): UserDetailResponse {
        val user = userRepository.findWithDetailsById(id)
            ?: throw ResourceNotFoundException("User with ID $id not found")
        return user.toDetailResponse()
    }
}
