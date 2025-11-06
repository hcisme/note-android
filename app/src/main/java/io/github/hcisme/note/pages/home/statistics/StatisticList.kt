package io.github.hcisme.note.pages.home.statistics

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.Dialog
import io.github.hcisme.note.components.Empty
import io.github.hcisme.note.components.HorizontalDragListItem
import io.github.hcisme.note.enums.CompletionStatusEnum
import io.github.hcisme.note.navigation.navigateToTodoForm
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.noRippleClickable
import io.github.hcisme.note.utils.withBadge

@Composable
fun StatisticList() {
    val navHostController = LocalNavController.current
    val context = LocalContext.current
    val statisticsVM = viewModel<StatisticsViewModel>(context as ComponentActivity)
    // ui 删除框相关
    var deleteDialogVisible by remember { mutableStateOf(false) }
    var currentSelectTodoId by remember { mutableStateOf<Long?>(null) }
    var openedItemId by remember { mutableStateOf<Long?>(null) }

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

        itemsIndexed(
            items = statisticsVM.statisticTodoList,
            key = { _, item -> item.id }
        ) { index, item ->
            HorizontalDragListItem(
                actionWidth = 160.dp,
                isOpen = openedItemId == item.id,
                onOpenChange = { openedItemId = if (it) item.id else null },
                mainContent = {
                    ListItem(
                        modifier = Modifier.noRippleClickable {
                            navHostController.navigateToTodoForm(id = item.id)
                        },
                        headlineContent = {
                            Text(
                                text = item.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
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
                },
                menuContent = {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable {
                                navHostController.navigateToTodoForm(id = item.id)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "编辑",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.error)
                            .clickable {
                                currentSelectTodoId = item.id
                                deleteDialogVisible = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "删除",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
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
