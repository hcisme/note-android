package io.github.hcisme.note.pages.home.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.TimelineTaskItem
import io.github.hcisme.note.utils.tasks

@Composable
fun TaskPage(modifier: Modifier = Modifier) {
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
                .padding(contentPadding)
        ) {
            HorizontalDateBar()

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                itemsIndexed(taskVM.todoList) { index, it ->
                    TimelineTaskItem(it, isCurrent = index == 0, isLast = index == tasks.size - 1)
                }
            }
        }
    }
}
