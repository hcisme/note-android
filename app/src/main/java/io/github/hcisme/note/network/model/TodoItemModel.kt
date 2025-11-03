package io.github.hcisme.note.network.model

data class TodoItemModel(
    var id: Long,
    var userId: String,
    var title: String,
    var content: String,
    var completed: Int,
    var startTime: String,
    var endTime: String? = null,
    var createdTime: String,
    var updatedTime: String
)

data class EditTodoItemVO(
    val id: Long? = null,
    val title: String,
    val content: String,
    val completed: Int,
    val startTime: String,
    val endTime: String? = null
)

data class TodoItemFormData(
    val id: Long? = null,
    var title: String = "",
    var content: String = "",
    var completed: Int = 0,
    var startTime: String = "",
    var endTime: String? = null
) {
    fun validate(): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (title.isEmpty()) {
            errors["title"] = "标题不能为空"
        } else if (title.length >= 20) {
            errors["title"] = "昵称长度不能超过20个字符"
        }

        if (content.isEmpty()) {
            errors["content"] = "描述不能为空"
        } else if (content.length >= 500) {
            errors["content"] = "描述不能超过500个字符"
        }

        if (startTime.isEmpty()) {
            errors["startTime"] = "开始时间不能为空"
        }

//        if (endTime.isEmpty()) {
//            errors["endTime"] = "结束时间不能为空"
//        }

        return errors
    }
}
