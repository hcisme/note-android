package io.github.hcisme.note.pages.todoform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.hcisme.note.network.TodoItemService
import io.github.hcisme.note.network.safeRequestCall
import kotlinx.coroutines.launch

class TodoFormViewModel : ViewModel() {
    var item by mutableStateOf(TodoItem())

    fun onValuesChange(item: TodoItem) {
        this.item = item
    }

    fun getTodoItem(id: Long) {
        viewModelScope.launch {
            safeRequestCall(
                call = { TodoItemService.getTodoById(id) },
                onSuccess = { result ->
                    val data = result.data ?: return@safeRequestCall
                    item = item.copy(
                        id = data.id,
                        title = data.title,
                        content = data.content,
                        completed = data.completed,
                        startTime = data.startTime,
                        endTime = data.endTime
                    )
                }
            )
        }
    }

    fun submit() {}
}

data class TodoItem(
    val id: Long? = null,
    var title: String = "",
    var content: String = "",
    var completed: Int = 0,
    var startTime: String = "",
    var endTime: String = ""
) {
    fun validate(): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (title.isEmpty()) {
            errors["title"] = "标题不能为空"
        } else if (title.length > 20) {
            errors["title"] = "昵称长度不能超过20个字符"
        }

        if (content.isEmpty()) {
            errors["content"] = "描述不能为空"
        }

        if (startTime.isEmpty()) {
            errors["startTime"] = "开始时间不能为空"
        }

        if (endTime.isEmpty()) {
            errors["endTime"] = "结束时间不能为空"
        }

        return errors
    }
}
