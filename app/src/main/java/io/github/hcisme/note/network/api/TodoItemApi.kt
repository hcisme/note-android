package io.github.hcisme.note.network.api

import io.github.hcisme.note.network.model.BaseResult
import io.github.hcisme.note.network.model.EditTodoItemVO
import io.github.hcisme.note.network.model.TodoItemModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TodoItemApi {
    @GET("/api/todoItem/list")
    suspend fun getList(@Query("time") time: String): BaseResult<List<TodoItemModel>>

    @DELETE("/api/todoItem/{id}")
    suspend fun deleteTodoItem(@Path("id") id: Long): BaseResult<Any?>

    @POST("/api/todoItem/createItem")
    suspend fun createItem(@Body item: EditTodoItemVO): BaseResult<Any?>

    @PUT("/api/todoItem/updateItem")
    suspend fun updateItem(@Body item: EditTodoItemVO): BaseResult<Any?>

    @GET("/api/todoItem/{id}")
    suspend fun getTodoById(@Path("id") id: Long): BaseResult<TodoItemModel?>
}