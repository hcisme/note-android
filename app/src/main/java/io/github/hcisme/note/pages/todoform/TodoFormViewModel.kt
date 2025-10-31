package io.github.hcisme.note.pages.todoform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.hcisme.note.components.NotificationManager
import io.github.hcisme.note.enums.Message
import io.github.hcisme.note.network.TodoItemService
import io.github.hcisme.note.network.model.EditTodoItemVO
import io.github.hcisme.note.network.model.TodoItemFormData
import io.github.hcisme.note.network.safeRequestCall
import kotlinx.coroutines.launch

class TodoFormViewModel : ViewModel() {
    var item by mutableStateOf(TodoItemFormData())
    var haveChangedForm by mutableStateOf(false)
    var loading by mutableStateOf(false)
    var errorMap by mutableStateOf(mapOf<String, String>())
    var backDialogVisible by mutableStateOf(false)

    fun onValuesChange(item: TodoItemFormData) {
        this.item = item
        haveChangedForm = true
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

    fun submit(onSuccess: () -> Unit = {}) {
        errorMap = item.validate()
        if (errorMap.isNotEmpty()) return

        loading = true
        val newItem = item.let {
            EditTodoItemVO(
                id = it.id,
                title = it.title,
                content = it.content,
                completed = it.completed,
                startTime = it.startTime,
                endTime = it.endTime
            )
        }

        viewModelScope.launch {
            safeRequestCall(
                call = {
                    if (newItem.id == null) {
                        TodoItemService.createItem(newItem)
                    } else {
                        TodoItemService.updateItem(newItem)
                    }
                },
                onFinally = { loading = false },
                onSuccess = {
                    NotificationManager.showNotification(
                        if (newItem.id == null) Message.ADD_SUCCESS.message else Message.EDIT_SUCCESS.message
                    )
                    haveChangedForm = false
                    onSuccess()
                }
            )
        }
    }
}
