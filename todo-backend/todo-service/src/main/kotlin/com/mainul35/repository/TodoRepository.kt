package com.mainul35.repository

import com.mainul35.entity.TodoEntity
import com.mainul35.entity.TodoStatus
import com.mainul35.resources.data.response.Todo
import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Response

@ApplicationScoped
class TodoRepository : PanacheRepository<TodoEntity> {

    fun findByTitle(title: String) : Uni<TodoEntity> {
        return find("title", title).firstResult<TodoEntity>();
    }

    fun findAllWithStatusInProgress(): Uni<List<TodoEntity>> {
        return list("status", TodoStatus.IN_PROGRESS)
    }

    fun findAllWithStatusDone(): Uni<List<TodoEntity>> {
        return list("status", TodoStatus.DONE)
    }

    fun findAllWithStatusToDo(): Uni<List<TodoEntity>> {
        return list("status", TodoStatus.TODO)
    }

    fun deleteSafe(id: Long): Uni<Long> {
        return delete("id", id)
    }

    fun save(todo: TodoEntity): Uni<Response> {
        if (todo.title.isBlank()) {
            return Uni.createFrom().item { Response.status(Response.Status.NO_CONTENT).build() }
        }
        return Panache.withTransaction { todo.persist<TodoEntity>() }
            .replaceWith { Response.ok().status(Response.Status.CREATED).build() }
    }

    fun update (id: Long, todo: TodoEntity): Uni<Response> {
        return Panache.withTransaction { this.findById(id) }
            .onItem().ifNotNull().invoke { entity ->
                {
                    entity.title = todo.title
                    entity.status = todo.status
                    entity.description = todo.description
                    entity.updatedDateTime = todo.updatedDateTime
                }
            }
            .onItem().ifNotNull().transform { entity -> Response.ok(entity).build() }
            .onItem().ifNull().continueWith { Response.status(Response.Status.NOT_FOUND).build() }
    }
}