package com.pause.backend.services

import com.pause.backend.exceptions.exceptions.DuplicateResourceException
import com.pause.backend.exceptions.exceptions.InvalidRequestException
import com.pause.backend.exceptions.exceptions.ResourceNotFoundException
import com.pause.backend.models.entities.Pause
import com.pause.backend.models.entities.User
import com.pause.backend.models.requests.CreatePauseRequest
import com.pause.backend.repositories.PauseRepository
import com.pause.backend.repositories.UserRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.LocalDateTime
import java.util.*

class PauseServiceTest {

    private lateinit var pauseRepository: PauseRepository
    private lateinit var userRepository: UserRepository
    private lateinit var pauseService: PauseService

    @BeforeEach
    fun setUp() {
        pauseRepository = mock(PauseRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        pauseService = PauseService(pauseRepository, userRepository)
    }

    @Test
    fun should_create_a_pause() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val request = CreatePauseRequest(
            userId = 1L,
            durationMinutes = 5,
            type = "active",
            source = "manual",
            clientEventId = "evt-1",
            timestamp = LocalDateTime.now()
        )
        val pause = Pause(
            user = user,
            durationMinutes = request.durationMinutes,
            type = request.type,
            source = request.source,
            clientEventId = request.clientEventId,
            timestamp = request.timestamp
        )

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
        `when`(pauseRepository.existsByClientEventId("evt-1")).thenReturn(false)
        `when`(pauseRepository.save(any(Pause::class.java))).thenReturn(pause)

        val result = pauseService.createPause(request)

        assertEquals(5, result.durationMinutes)
        assertEquals("active", result.type)
        assertEquals("manual", result.source)
        assertEquals("evt-1", result.clientEventId)
    }

    @Test
    fun should_throw_exception_when_duration_invalid() {
        val request = CreatePauseRequest(
            userId = 1L, durationMinutes = 0, timestamp = LocalDateTime.now()
        )
        assertThrows<InvalidRequestException> {
            pauseService.createPause(request)
        }
    }

    @Test
    fun should_throw_duplicate_when_clientEventId_exists() {
        val request = CreatePauseRequest(
            userId = 1L, durationMinutes = 5, clientEventId = "evt-dup", timestamp = LocalDateTime.now()
        )
        `when`(pauseRepository.existsByClientEventId("evt-dup")).thenReturn(true)

        assertThrows<DuplicateResourceException> {
            pauseService.createPause(request)
        }
        verify(pauseRepository, never()).save(any(Pause::class.java))
    }

    @Test
    fun should_throw_exception_when_user_not_found_on_create() {
        val request = CreatePauseRequest(
            userId = 1L,
            durationMinutes = 10,
            type = "active",
            source = "android",
            clientEventId = null,
            timestamp = LocalDateTime.now()
        )

        `when`(pauseRepository.existsByClientEventId(anyString())).thenReturn(false)

        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> { pauseService.createPause(request) }
    }

    @Test
    fun should_return_all_pauses() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val pause = Pause(user = user, durationMinutes = 10, timestamp = LocalDateTime.now())
        `when`(pauseRepository.findAll()).thenReturn(listOf(pause))

        val result = pauseService.getAll()

        assertEquals(1, result.size)
        assertEquals(10, result[0].durationMinutes)
    }

    @Test
    fun should_return_pause_by_id() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val pause = Pause(user = user, durationMinutes = 8, timestamp = LocalDateTime.now())
        `when`(pauseRepository.findById(1L)).thenReturn(Optional.of(pause))

        val result = pauseService.getById(1L)

        assertEquals(8, result.durationMinutes)
    }

    @Test
    fun should_throw_exception_when_pause_not_found_by_id() {
        `when`(pauseRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { pauseService.getById(1L) }
    }

    @Test
    fun should_delete_pause() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val pause = Pause(user = user, durationMinutes = 12, timestamp = LocalDateTime.now())
        `when`(pauseRepository.findById(1L)).thenReturn(Optional.of(pause))

        pauseService.delete(1L)

        verify(pauseRepository).delete(pause)
    }

    @Test
    fun should_throw_exception_when_deleting_non_existent_pause() {
        `when`(pauseRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { pauseService.delete(1L) }
    }
}