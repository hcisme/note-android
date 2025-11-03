package io.github.hcisme.note.pages.home.statistics

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.Dialog
import io.github.hcisme.note.components.Empty
import io.github.hcisme.note.enums.CompletionStatusEnum
import io.github.hcisme.note.navigation.navigateToTodoForm
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.withBadge

@Composable
fun StatisticList() {
    val navHostController = LocalNavController.current
    val context = LocalContext.current
    val statisticsVM = viewModel<StatisticsViewModel>(context as ComponentActivity)
    // ui 删除框相关
    var deleteDialogVisible by remember { mutableStateOf(false) }
    var currentSelectTodoId by remember { mutableStateOf<Long?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (statisticsVM.statisticTodoList.isEmpty()) {
            item {
                Empty()
            }
        }

        itemsIndexed(statisticsVM.statisticTodoList) { index, item ->
            ListItem(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            navHostController.navigateToTodoForm(id = item.id)
                        },
                        onLongClick = {
                            currentSelectTodoId = item.id
                            deleteDialogVisible = true
                        }
                    ),
                headlineContent = {
                    Text(
                        text = item.title,
                        modifier = Modifier.withBadge(
                            badgeColor = CompletionStatusEnum.getByStatus(item.completed)!!.color,
                            offset = { Offset(size.width + 16, 0f) }
                        )
                    )
                },
                supportingContent = {
                    val endTime = if (item.endTime != null) "——${item.endTime}" else ""
                    Text(
                        text = "${item.startTime}${endTime}",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                shadowElevation = if (statisticsVM.statisticTodoList.size == index + 1) 0.dp else 8.dp
            )
        }
    }

    Dialog(
        visible = deleteDialogVisible,
        confirmButtonText = "确定",
        cancelButtonText = "取消",
        onConfirm = {
            currentSelectTodoId?.let {
                deleteDialogVisible = false
                statisticsVM.deleteTodoItemById(
                    id = it,
                    onSuccess = {
                        currentSelectTodoId = null
                    }
                )
            }
        },
        onDismissRequest = { deleteDialogVisible = false }
    ) {
        Text(text = "确定删除吗")
    }
}
