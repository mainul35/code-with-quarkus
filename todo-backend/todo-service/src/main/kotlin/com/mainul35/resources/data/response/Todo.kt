package com.mainul35.resources.data.response

import java.time.LocalDateTime

data class Todo(
    val title: String,
    val description: String,
    val createdDateTime: LocalDateTime
)
