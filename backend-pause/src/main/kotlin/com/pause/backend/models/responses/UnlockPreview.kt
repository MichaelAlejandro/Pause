package com.pause.backend.models.responses

data class UnlockPreview(
    val itemId: String,
    val requirementText: String,
    val progress: Int,
    val required: Int
)