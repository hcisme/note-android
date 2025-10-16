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
