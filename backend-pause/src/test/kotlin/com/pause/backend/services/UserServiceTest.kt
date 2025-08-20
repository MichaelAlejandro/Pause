package com.pause.backend.services

import com.pause.backend.exceptions.exceptions.DuplicateResourceException
import com.pause.backend.exceptions.exceptions.ResourceNotFoundException
import com.pause.backend.models.entities.User
import com.pause.backend.models.requests.CreateUserRequest
import com.pause.backend.repositories.PauseRepository
import com.pause.backend.repositories.PetRepository
import com.pause.backend.repositories.ReviewRepository
import com.pause.backend.repositories.UserRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var pauseRepository: PauseRepository
    private lateinit var petRepository: PetRepository
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        pauseRepository = mock(PauseRepository::class.java)
        petRepository = mock(PetRepository::class.java)
        reviewRepository = mock(ReviewRepository::class.java)

        userService = UserService(
            userRepository,
            petRepository,
            pauseRepository,
            reviewRepository
        )
    }

    @Test
    fun should_create_a_new_user() {
        val request = CreateUserRequest("uid123", "Michael", "maalejandro@puce.edu")
        val user = request.toEntity()

        `when`(userRepository.existsByUid("uid123")).thenReturn(false)
        `when`(userRepository.existsByEmail("maalejandro@puce.edu")).thenReturn(false)
        `when`(userRepository.save(any(User::class.java))).thenReturn(user)

        val result = userService.createUser(request)

        assertEquals("Michael", result.userName)
        assertEquals("uid123", result.uid)
    }

    @Test
    fun should_throw_duplicate_when_uid_exists() {
        val request = CreateUserRequest("uid123", "Michael", "a@b.com")
        `when`(userRepository.existsByEmail(anyString())).thenReturn(false) // <- explícito
        `when`(userRepository.existsByUid("uid123")).thenReturn(true)      // <- dup

        assertThrows<DuplicateResourceException> { userService.createUser(request) }
        verify(userRepository, never()).save(any(User::class.java))
    }

    @Test
    fun should_throw_duplicate_when_email_exists() {
        val request = CreateUserRequest("uidX", "Michael", "a@b.com")
        `when`(userRepository.existsByUid("uidX")).thenReturn(false)
        `when`(userRepository.existsByEmail("a@b.com")).thenReturn(true)

        assertThrows<DuplicateResourceException> { userService.createUser(request) }
        verify(userRepository, never()).save(any(User::class.java))
    }

    @Test
    fun should_return_all_users() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        `when`(userRepository.findAll()).thenReturn(listOf(user))

        val result = userService.getAllUsers()

        assertEquals(1, result.size)
        assertEquals("Michael", result[0].userName)
    }

    @Test
    fun should_return_user_by_id() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))

        val result = userService.getUserById(1L)

        assertEquals("uid123", result.uid)
    }

    @Test
    fun should_throw_exception_when_user_not_found() {
        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { userService.getUserById(1L) }
    }

    @Test
    fun should_update_user() {
        val existing = User(uid = "uid123", userName = "Primero", email = "primero@puce.edu.ec")
        val request = CreateUserRequest("uid123", "Segundo", "segundo@puce.edu.ec")

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(existing))
        `when`(userRepository.save(existing)).thenReturn(existing)

        val result = userService.updateUser(1L, request)

        assertEquals("Segundo", result.userName)
        assertEquals("segundo@puce.edu.ec", result.email)
    }

    @Test
    fun should_throw_exception_when_updating_non_existent_user() {
        val request = CreateUserRequest("uid123", "Name", "email@puce.edu.ec")
        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { userService.updateUser(1L, request) }
    }

    @Test
    fun should_delete_user() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))

        userService.deleteUser(1L)
        verify(userRepository).delete(user)
    }

    @Test
    fun should_throw_exception_when_deleting_non_existent_user() {
        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { userService.deleteUser(1L) }
    }
}