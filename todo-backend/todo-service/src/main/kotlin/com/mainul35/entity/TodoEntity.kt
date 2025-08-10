package com.mainul35.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import io.quarkus.hibernate.reactive.panache.PanacheEntity
import java.time.LocalDateTime

@Entity
@Table(name = "todos")
class TodoEntity(
    var createdDateTime: LocalDateTime,
    var updatedDateTime: LocalDateTime,
    @Enumerated(EnumType.STRING)
    var status: TodoStatus,
    var description: String,
    var title: String
) : PanacheEntity() {
    // JPA requires a no-arg constructor; keep it at least protected
    protected constructor() : this(LocalDateTime.now(), LocalDateTime.now(), TodoStatus.TODO, "", "")

    override fun toString(): String =
        "id=$id,\n" +
                "title=$title,\n" +
                "description=$description,\n" +
                "status=$status,\n" +
                "createdDateTime=$createdDateTime\n" +
                "updatedDateTime=$updatedDateTime"
}
