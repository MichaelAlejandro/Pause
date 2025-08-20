package com.pause.backend.controllers

import com.pause.backend.models.requests.CreateUserRequest
import com.pause.backend.models.responses.UserDetailResponse
import com.pause.backend.models.responses.UserResponse
import com.pause.backend.routes.Routes
import com.pause.backend.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Routes.BASE_URL + Routes.USERS)
class UserController(
    private val userService: UserService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody request: CreateUserRequest): UserResponse =
        userService.createUser(request)

    @GetMapping
    fun getAllUsers(): List<UserResponse> = userService.getAllUsers()

    @GetMapping(Routes.ID)
    fun getUserById(@PathVariable id: Long): UserResponse = userService.getUserById(id)

    @PutMapping(Routes.ID)
    fun updateUser(@PathVariable id: Long, @RequestBody request: CreateUserRequest): UserResponse =
        userService.updateUser(id, request)

    @DeleteMapping(Routes.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable id: Long) = userService.deleteUser(id)

    @GetMapping(Routes.USER_DETAILS)
    fun getDetails(@PathVariable id: Long): UserDetailResponse =
        userService.getUserDetails(id)
}