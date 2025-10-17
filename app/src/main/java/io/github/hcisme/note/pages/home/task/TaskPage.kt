package io.github.hcisme.note.pages.home.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.Dialog
import io.github.hcisme.note.components.TimelineTaskItem
import io.github.hcisme.note.utils.LocalNotificationManager
import io.github.hcisme.note.utils.tasks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPage(modifier: Modifier = Modifier) {
    val notificationManager = LocalNotificationManager.current
    val taskVM = viewModel<TaskViewModel>()

    LaunchedEffect(Unit) {
        if (taskVM.todoList.isEmpty()) {
            taskVM.getTodoList()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { HomeTaskTopBar() }
    ) { innerPadding ->
        val contentPadding = PaddingValues(
            start = innerPadding.calculateLeftPadding(LayoutDirection.Ltr),
            top = innerPadding.calculateTopPadding(),
            end = innerPadding.calculateRightPadding(LayoutDirection.Ltr),
            bottom = 0.dp
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(contentPadding)
        ) {
            HorizontalDateBar()

            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.background),
                isRefreshing = taskVM.isLoading,
                onRefresh = { taskVM.getTodoList() }
            ) {
                var currentSelectTodoId by remember { mutableStateOf<Int?>(null) }

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(taskVM.todoList) { index, it ->
                        TimelineTaskItem(
                            it,
                            isCurrent = index == 0,
                            isLast = index == tasks.size - 1,
                            onClickDelete = {
                                currentSelectTodoId = it.id
                                taskVM.deleteDialogVisible = true
                            }
                        )
                    }
                }

                Dialog(
                    visible = taskVM.deleteDialogVisible,
                    confirmButtonText = "确定",
                    cancelButtonText = "取消",
                    onConfirm = {
                        currentSelectTodoId?.let {
                            taskVM.deleteDialogVisible = false
                            taskVM.deleteTodoItemById(it) {
                                currentSelectTodoId = null
                                notificationManager.showNotification("删除成功")
                            }
                        }
                    },
                    onDismissRequest = { taskVM.deleteDialogVisible = false }
                ) {
                    Text(text = "确定删除吗")
                }
            }
        }
    }
}
