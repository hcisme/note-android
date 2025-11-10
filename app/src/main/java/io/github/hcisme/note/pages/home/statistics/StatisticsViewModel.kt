package io.github.hcisme.note.pages.home.statistics

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
import io.github.hcisme.note.network.StatisticService
import io.github.hcisme.note.network.TodoItemService
import io.github.hcisme.note.network.model.TodoItemModel
import io.github.hcisme.note.network.safeRequestCall
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class StatisticsViewModel : ViewModel() {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    var currentDate by mutableStateOf(
        SimpleDate(
            year = today.year,
            monthNumber = today.monthNumber
        )
    )
        private set
    var sortEnum by mutableStateOf(SortOrderEnum.ASC)
    var pies by mutableStateOf(listOf<Pie>())
    val statisticTodoList = mutableStateListOf<TodoItemModel>()

    fun onPieClick(pie: Pie) {
        pies = pies.map {
            if (it.label == pie.label) it.copy(selected = !it.selected) else it.copy(selected = false)
        }
        getStatisticTodoList()
    }

    fun changeDate(date: SimpleDate = currentDate) {
        currentDate = date

        getTodoCompletedCount()
    }

    fun changeOrder(sortEnum: SortOrderEnum = this.sortEnum) {
        this.sortEnum = sortEnum.not
        getStatisticTodoList()
    }

    fun getTodoCompletedCount() {
        val month = if (currentDate.monthNumber in 1..12)
            currentDate.monthNumber.toString().padStart(2, '0')
        else
            "00"
        val timeStr = "${currentDate.year}-${month}-00"

        viewModelScope.launch {
            safeRequestCall(
                call = { StatisticService.getCompletionStats(time = timeStr) },
                onSuccess = { result ->
                    val list = result.data
                    val selectedLabel = pies.find { it.selected }?.label

                    val newList = list.map { item ->
                        Pie(
                            label = item.completed.toString(),
                            data = item.count.toDouble(),
                            color = CompletionStatusEnum.getByStatus(item.completed)!!.color,
                            selected = item.completed.toString() == selectedLabel
                        )
                    }
                    pies = newList
                },
                onFinally = {
                    getStatisticTodoList()
                }
            )
        }
    }

    fun getStatisticTodoList() {
        val month = if (currentDate.monthNumber in 1..12)
            currentDate.monthNumber.toString().padStart(2, '0')
        else
            "00"
        val timeStr = "${currentDate.year}-${month}-00"
        val completed = pies.find { it.selected }?.let { it.label!!.toInt() }

        viewModelScope.launch {
            safeRequestCall(
                call = {
                    TodoItemService.getList(
                        time = timeStr,
                        completed = completed,
                        sort = sortEnum.status
                    )
                },
                onSuccess = { result ->
                    val list = result.data
                    statisticTodoList.apply {
                        this@apply.clear()
                        this@apply.addAll(list)
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
                    getTodoCompletedCount()
                    onSuccess()
                }
            )
        }
    }
}

data class SimpleDate(
    val year: Int,
    val monthNumber: Int
)
