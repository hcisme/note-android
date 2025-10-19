package io.github.hcisme.note.network.model

data class TodoItemModel(
    var id: Long,
    var userId: String,
    var title: String,
    var content: String,
    var completed: Int,
    var startTime: String,
    var endTime: String,
    var createdTime: String,
    var updatedTime: String
)

data class CreateTodoItemVO(
    val title: String,
    val content: String,
    val completed: Int,
    val startTime: String,
    val endTime: String
)

data class UpdateTodoItemVO(
    val id: Long,
    val title: String,
    val content: String,
    val completed: Int,
    val startTime: String,
    val endTime: String
)
