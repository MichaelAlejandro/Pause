package com.pause.frontend.utils

import java.time.OffsetDateTime

object TimeUtils {
    fun nowIso(): String = OffsetDateTime.now().toString()
    fun newEventId(prefix: String) = "$prefix-${System.currentTimeMillis()}"
}