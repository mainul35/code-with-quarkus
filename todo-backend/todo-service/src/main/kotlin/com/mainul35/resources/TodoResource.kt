package com.mainul35.resources;


import com.mainul35.entity.TodoEntity
import com.mainul35.service.TodoService
import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class TodoResource @Inject constructor(
    private val todoService: TodoService
) {
    @GET
    @Path("/init")
    fun initializeDatabase(): Uni<Response> {
        return todoService.init()
    }


    @GET
    fun getTodos(): Uni<Response> {
        return todoService.getAllTodos().onItem().transform { todos -> Response.ok().entity(todos).build() }
    }

    @POST
    fun addTodo(todo: TodoEntity): Uni<Response> = todoService.addTodo(todo)
}