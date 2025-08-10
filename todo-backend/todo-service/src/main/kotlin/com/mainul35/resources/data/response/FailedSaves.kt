package com.mainul35.resources.data.response

import com.mainul35.entity.TodoEntity

data class FailedSave(val failedItem: TodoEntity, val message: String)
