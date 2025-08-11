package com.mainul35.resources.data.response

import com.mainul35.entity.TodoStatus
import java.time.LocalDateTime

data class Todo(
    var createdDateTime: LocalDateTime,
    var updatedDateTime: LocalDateTime,
    var status: TodoStatus,
    var description: String,
    var title: String,
    var id: Long
)
