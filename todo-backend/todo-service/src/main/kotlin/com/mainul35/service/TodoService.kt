package com.mainul35.service


import com.mainul35.entity.TodoEntity
import com.mainul35.entity.TodoStatus
import com.mainul35.repository.TodoRepository
import com.mainul35.resources.data.response.FailedSave
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import org.jboss.logging.Logger
import java.time.LocalDateTime

@ApplicationScoped
class TodoService @Inject constructor(
    @Inject
    private val todoRepository: TodoRepository
) {

    private val LOG: Logger = Logger.getLogger(this.javaClass.name)

    fun init (): Uni<Response> {
        val now = LocalDateTime.now()
        val initialTodos = listOf(
            TodoEntity(
                title = "Grocery Shopping",
                description = "Buy milk, eggs, and bread from the store.",
                status = TodoStatus.TODO,
                createdDateTime = now.minusDays(2),
                updatedDateTime = now.minusDays(2)
            ),
            TodoEntity(
                title = "Pay Bills",
                description = "Pay the electricity and phone bills.",
                status = TodoStatus.IN_PROGRESS,
                createdDateTime = now.minusDays(2),
                updatedDateTime = now.minusDays(2)
            ),
            TodoEntity(
                title = "Learn Java",
                description = "Study Java Stream API and concurrency.",
                status = TodoStatus.DONE,
                createdDateTime = now.minusDays(3),
                updatedDateTime = now.minusDays(3)
            ),
            TodoEntity(
                title = "Grocery List",
                description = "Apples, bananas, oranges, and grapes.",
                status = TodoStatus.TODO,
                createdDateTime = now.minusDays(4),
                updatedDateTime = now.minusDays(4),
            ),
            TodoEntity(
                title = "",
                description = "Apples, bananas, oranges, and grapes.",
                status = TodoStatus.TODO,
                createdDateTime = now.minusDays(4),
                updatedDateTime = now.minusDays(4),
            ),
            TodoEntity(
                title = "Meeting with John",
                description = "Discuss the project requirements and timeline.\nNeed to prepare a presentation.",
                status = TodoStatus.IN_PROGRESS,
                createdDateTime = now.minusDays(5),
                updatedDateTime = now.minusDays(5),
            ),
            TodoEntity(
                title = "Grocery Shopping",
                description = "Buy milk, eggs, and bread from the store.",
                status = TodoStatus.DONE,
                createdDateTime = now.minusDays(1),
                updatedDateTime = now.minusDays(1)
            )
        )

        /*return Multi.createFrom().iterable(initialTodos)
            .onItem().transformToUniAndConcatenate { todo ->
                todoRepository.save(todo)
                    .flatMap { resp ->
                        if (resp.status == Response.Status.NO_CONTENT.statusCode) {
                            val ex = ServiceException("No title provided for id '${todo.id}'")
                            LOG.error("Save skipped for id: '${todo.id}': ${ex.message}", ex)
                            Uni.createFrom().voidItem()
                        } else {
                            Uni.createFrom().voidItem()
                        }
                    }
                // Let non-NO_CONTENT failures bubble up to fail init(); add transforms if you want custom logging
            }
            .collect().asList()
            .replaceWithVoid()*/

        // Sequential insert
        return Multi.createFrom().iterable(initialTodos)
            .onItem().transformToUniAndConcatenate { todo ->
                todoRepository.save(todo)
                    .flatMap { resp ->
                        if (resp.status == Response.Status.NO_CONTENT.statusCode) {
                            val msg = "No title provided for id '${todo.id ?: "transient"}'"
                            LOG.error("Save skipped for id='${todo.id}': $msg")
                            Uni.createFrom().item(FailedSave(todo, msg))
                        } else {
                            Uni.createFrom().nullItem<FailedSave>() // success -> no failure record
                        }
                    }
                    // Optional: if you want unexpected errors to be reported as failures instead of failing the whole init:
                    .onFailure().invoke { t ->
                        LOG.errorf(t, "Unexpected failure saving id=%s title='%s'", todo.id, todo.title)
                    }
                    .onFailure().recoverWithItem { t ->
                        FailedSave(todo, "Unexpected error: ${t.message ?: t.javaClass.simpleName}")
                    }
            }
            .collect().asList()
            .onItem().transform { list -> list.filterNotNull() }
            .onItem().transform { failedSaves ->
                if (failedSaves.isEmpty()) {
                    Response.ok(mapOf("message" to "All todos inserted")).build()
                } else {
// 207 Multi-Status is suitable for partial success; build with raw code
                    Response.status(207).entity(failedSaves).build()
                }
            }
    }

    fun getAllTodos(): Uni<List<TodoEntity>> = todoRepository.findAll().list()

    fun addTodo(todo: TodoEntity): Uni<Response> = todoRepository.save(todo)

}

