package io.github.hcisme.note.network.api

import io.github.hcisme.note.network.BaseResult
import io.github.hcisme.note.network.model.TodoItemModel
import retrofit2.http.GET
import retrofit2.http.Query

interface TodoItemApi {
    @GET("/api/todoItem/list")
    suspend fun getList(@Query("time") time: String): BaseResult<List<TodoItemModel>>
}