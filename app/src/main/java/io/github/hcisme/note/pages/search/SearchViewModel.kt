package io.github.hcisme.note.pages.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.hcisme.note.components.NotificationManager
import io.github.hcisme.note.enums.CompletionStatusEnum
import io.github.hcisme.note.enums.Message
import io.github.hcisme.note.enums.SortOrderEnum
import io.github.hcisme.note.network.TodoItemService
import io.github.hcisme.note.network.model.TodoItemModel
import io.github.hcisme.note.network.safeRequestCall
import io.github.hcisme.note.pages.home.statistics.SimpleDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SearchViewModel : ViewModel() {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    var searchWord by mutableStateOf("")

    // 搜索的过滤条件
    var currentDate by mutableStateOf(
        SimpleDate(year = today.year, monthNumber = today.monthNumber)
    )
    var completedEnum by mutableStateOf<CompletionStatusEnum?>(CompletionStatusEnum.COMPLETED)
    var sortEnum by mutableStateOf<SortOrderEnum?>(SortOrderEnum.ASC)
    val searchedTodoList = mutableStateListOf<TodoItemModel>()

    // ui相关
    var expanded by mutableStateOf(false)

    fun search(
        word: String = searchWord,
        completed: CompletionStatusEnum? = completedEnum,
        sortEnum: SortOrderEnum? = this.sortEnum,
        date: SimpleDate = currentDate
    ) {
        searchWord = word
        this.completedEnum = completed
        this.sortEnum = sortEnum
        this.currentDate = date

        getSearchResultTodoList()
    }

    fun getSearchResultTodoList() {
        if (searchWord.isEmpty()) return
        val timeStr = currentDate.toString()
        expanded = false

        viewModelScope.launch {
            safeRequestCall(
                call = {
                    TodoItemService.getList(
                        time = timeStr,
                        completed = completedEnum?.status,
                        sort = sortEnum?.status,
                        searchWord = searchWord
                    )
                },
                onSuccess = { result ->
                    val list = result.data
                    searchedTodoList.apply {
                        this@apply.clear()
                        this@apply.addAll(list)
                        expanded = true
                    }
                }
            )
        }
    }

    fun deleteTodoItemById(id: Long, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            safeRequestCall(
                call = { withContext(Dispatchers.IO) { TodoItemService.deleteTodoItem(id) } },
                onSuccess = {
                    NotificationManager.showNotification(Message.DELETE_SUCCESS.message)
                    getSearchResultTodoList()
                    onSuccess()
                }
            )
        }
    }
}