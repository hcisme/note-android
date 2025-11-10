package io.github.hcisme.note.network.api

import io.github.hcisme.note.network.model.BaseResult
import io.github.hcisme.note.network.model.TodoCompletionStats
import retrofit2.http.GET
import retrofit2.http.Query

interface StatisticApi {
    @GET("/api/todoItem/getCompletionStats")
    suspend fun getCompletionStats(@Query("time") time: String): BaseResult<List<TodoCompletionStats>>
}