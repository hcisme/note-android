package io.github.hcisme.note.pages.home.task

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.hcisme.note.components.NotificationManager
import io.github.hcisme.note.enums.Message
import io.github.hcisme.note.network.TodoItemService
import io.github.hcisme.note.network.model.TodoItemModel
import io.github.hcisme.note.network.safeRequestCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class TaskViewModel : ViewModel() {
    private var getTodoListJob: Job? = null
    val today get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    var currentDate by mutableStateOf(today)
        private set
    val isToday by derivedStateOf { currentDate == today }
    val monthDates: List<LocalDate> by derivedStateOf {
        buildMonthDates(currentDate.year, currentDate.monthNumber)
    }
    var selectedTabIndex by mutableIntStateOf(
        monthDates.indexOfFirst { it == currentDate }.let { if (it >= 0) it else 0 }
    )
        private set
    var isLoading by mutableStateOf(false)
    val todoList = mutableStateListOf<TodoItemModel>()

    fun changeDate(index: Int = selectedTabIndex, date: LocalDate = currentDate) {
        selectedTabIndex = index
        currentDate = date

        getTodoListWithLoading()
    }

    fun getTodoListWithLoading() {
        getTodoListJob?.cancel()
        getTodoListJob = viewModelScope.launch {
            isLoading = true

            safeRequestCall(
                call = { withContext(Dispatchers.IO) { TodoItemService.getList(time = currentDate.toString()) } },
                onFinally = { isLoading = false },
                onSuccess = {
                    todoList.apply {
                        clear()
                        addAll(it.data)
                    }
                }
            )
        }
    }

    fun getTodoListWithoutLoading() {
        getTodoListJob?.cancel()
        getTodoListJob = viewModelScope.launch {
            safeRequestCall(
                call = { withContext(Dispatchers.IO) { TodoItemService.getList(time = currentDate.toString()) } },
                onSuccess = {
                    todoList.apply {
                        clear()
                        addAll(it.data)
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
                    getTodoListWithLoading()
                    onSuccess()
                }
            )
        }
    }

    private fun buildMonthDates(year: Int, month: Int): List<LocalDate> {
        val first = LocalDate(year, month, 1)
        // 取下个月的 1 号，然后回退一天得到本月最后一天
        val nextMonthFirst = first.plus(1, DateTimeUnit.MONTH)
        val last = nextMonthFirst.minus(1, DateTimeUnit.DAY)
        val days = mutableListOf<LocalDate>()
        var d = first
        while (d <= last) {
            days += d
            d = d.plus(1, DateTimeUnit.DAY)
        }
        return days
    }
}