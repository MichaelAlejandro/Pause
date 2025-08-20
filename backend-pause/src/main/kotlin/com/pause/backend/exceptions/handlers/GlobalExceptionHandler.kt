package com.pause.backend.exceptions.handlers

import com.pause.backend.exceptions.exceptions.*
import com.pause.backend.models.responses.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(code = "NOT_FOUND", message = ex.message ?: "Not found"))

    @ExceptionHandler(InvalidRequestException::class)
    fun handleInvalid(ex: InvalidRequestException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(code = "VALIDATION_ERROR", message = ex.message ?: "Invalid request", details = ex.details))

    @ExceptionHandler(DuplicateResourceException::class)
    fun handleDuplicate(ex: DuplicateResourceException) =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse(code = "DUPLICATE_RESOURCE", message = ex.message ?: "Duplicate resource"))

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(ex: UnauthorizedException) =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse(code = "UNAUTHORIZED", message = ex.message ?: "Unauthorized"))

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(ex: ForbiddenException) =
        ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse(code = "FORBIDDEN", message = ex.message ?: "Forbidden"))

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception) =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(code = "INTERNAL_ERROR", message = ex.message ?: "Unexpected error"))
}
