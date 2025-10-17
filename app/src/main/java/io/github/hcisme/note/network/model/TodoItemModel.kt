package io.github.hcisme.note.network.model

data class TodoItemModel(
    var id: Int,
    var userId: String,
    var title: String,
    var content: String,
    var isCompleted: Int,
    var startTime: String,
    var endTime: String,
    var createdTime: String,
    var updatedTime: String
)

data class CreateTodoItemVO(
    val title: String,
    val content: String,
    val startTime: String,
    val endTime: String
)

data class UpdateTodoItemVO(
    val id: Int,
    val title: String,
    val content: String,
    val isCompleted: Int,
    val startTime: String,
    val endTime: String
)
