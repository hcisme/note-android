package io.github.hcisme.note.pages.home.task

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.R
import io.github.hcisme.note.components.Dialog
import io.github.hcisme.note.components.TimelineTaskItem
import io.github.hcisme.note.navigation.navigateToTodoForm
import io.github.hcisme.note.utils.LocalNavController

@Composable
fun TaskPage(modifier: Modifier = Modifier) {
    val navHostController = LocalNavController.current
    val taskVM = viewModel<TaskViewModel>()

    LaunchedEffect(Unit) {
        taskVM.getTodoList()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
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

            if (taskVM.todoList.isEmpty() && !taskVM.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(64.dp),
                            painter = painterResource(R.drawable.nothing),
                            contentDescription = null
                        )
                        Text(
                            text = "暂无数据",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            } else {
                var currentSelectTodoId by remember { mutableStateOf<Long?>(null) }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(taskVM.todoList) { index, it ->
                        TimelineTaskItem(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            item = it,
                            isCurrent = index == 0,
                            isLast = index == taskVM.todoList.size - 1,
                            onClick = {
                                navHostController.navigateToTodoForm(id = it.id)
                            },
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
                            taskVM.deleteTodoItemById(
                                id = it,
                                onSuccess = {
                                    currentSelectTodoId = null
                                }
                            )
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
