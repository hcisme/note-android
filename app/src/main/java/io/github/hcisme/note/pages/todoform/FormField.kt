package io.github.hcisme.note.pages.todoform

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.DateTimePickerPopup
import io.github.hcisme.note.utils.formatWithPattern
import io.github.hcisme.note.utils.noRippleClickable
import io.github.hcisme.note.utils.toLocalDateTime
import io.github.hcisme.note.utils.withBadge

/**
 * 标题
 */
@Composable
fun TitleInputField() {
    val todoFormVM = viewModel<TodoFormViewModel>()

    Column {
        Text(
            "标题",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.withBadge(
                badgeText = "*",
                textColor = MaterialTheme.colorScheme.error,
                offset = { Offset(size.width + 4.dp.toPx(), 8.dp.toPx()) }
            )
        )
        OutlinedTextField(
            value = todoFormVM.item.title,
            onValueChange = {
                todoFormVM.onValuesChange(todoFormVM.item.copy(title = it))
            },
            placeholder = { Text("请输入标题") },
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            singleLine = true,
            isError = todoFormVM.errorMap.containsKey("title"),
            supportingText = {
                if (todoFormVM.errorMap.containsKey("title")) {
                    Text(todoFormVM.errorMap.getValue("title"))
                }
            }
        )
    }
}

/**
 * 是否完成
 */
@Composable
fun CompletionStatusField() {
    val todoFormVM = viewModel<TodoFormViewModel>()

    Column {
        Text(
            text = "完成状态",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.withBadge(
                badgeText = "*",
                textColor = MaterialTheme.colorScheme.error,
                offset = { Offset(size.width + 4.dp.toPx(), 8.dp.toPx()) }
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = todoFormVM.item.completed == 0,
                    onClick = {
                        todoFormVM.onValuesChange(todoFormVM.item.copy(completed = 0))
                    }
                )
                Text(
                    text = "未完成",
                    modifier = Modifier.noRippleClickable {
                        todoFormVM.onValuesChange(todoFormVM.item.copy(completed = 0))
                    }
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = todoFormVM.item.completed == 1,
                    onClick = { todoFormVM.onValuesChange(todoFormVM.item.copy(completed = 1)) }
                )
                Text(
                    text = "已完成",
                    modifier = Modifier.noRippleClickable {
                        todoFormVM.onValuesChange(todoFormVM.item.copy(completed = 1))
                    }
                )
            }
        }
    }
}

/**
 * 开始时间
 */
@Composable
fun StartTimePickerField() {
    val timePickerState = rememberTimePickerState()
    val todoFormVM = viewModel<TodoFormViewModel>()

    Column {
        Text(
            text = "开始时间",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.withBadge(
                badgeText = "*",
                textColor = MaterialTheme.colorScheme.error,
                offset = { Offset(size.width + 4.dp.toPx(), 8.dp.toPx()) }
            )
        )

        OutlinedTextField(
            value = todoFormVM.item.startTime,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("点击输入开始时间") },
            trailingIcon = {
                IconButton(onClick = { timePickerState.open() }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "选择开始日期"
                    )
                }
            },
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .onGloballyPositioned { coords -> timePickerState.coordsRef.set(coords) },
            isError = todoFormVM.errorMap.containsKey("startTime"),
            supportingText = {
                if (todoFormVM.errorMap.containsKey("startTime")) {
                    Text(text = todoFormVM.errorMap.getValue("startTime"))
                }
            }
        )
    }

    DateTimePickerPopup(
        visible = timePickerState.visible,
        selectedDateTime = if (todoFormVM.item.startTime.isNotEmpty()) todoFormVM.item.startTime.toLocalDateTime() else null,
        anchorBoundsIntOffset = timePickerState.anchorOffset,
        onDismiss = { timePickerState.close() },
        onDateTimeSelected = { dateTime ->
            todoFormVM.onValuesChange(todoFormVM.item.copy(startTime = dateTime.formatWithPattern()))
        }
    )

    BackHandler(enabled = timePickerState.visible) {
        timePickerState.visible = false
    }
}

/**
 * 结束时间
 */
@Composable
fun EndTimePickerField() {
    val timePickerState = rememberTimePickerState()
    val todoFormVM = viewModel<TodoFormViewModel>()

    Column {
        Text(
            text = "结束时间",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = todoFormVM.item.endTime ?: "",
            onValueChange = { },
            readOnly = true,
            placeholder = { Text("点击输入结束时间") },
            trailingIcon = {
                IconButton(onClick = { timePickerState.open() }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date"
                    )
                }
            },
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .onGloballyPositioned { coords -> timePickerState.coordsRef.set(coords) }
        )
    }

    DateTimePickerPopup(
        visible = timePickerState.visible,
        selectedDateTime = todoFormVM.item.endTime?.toLocalDateTime(),
        anchorBoundsIntOffset = timePickerState.anchorOffset,
        onDismiss = { timePickerState.close() },
        onDateTimeSelected = { dateTime ->
            todoFormVM.onValuesChange(todoFormVM.item.copy(endTime = dateTime.formatWithPattern()))
        }
    )

    BackHandler(enabled = timePickerState.visible) {
        timePickerState.visible = false
    }
}

/**
 * 描述信息
 */
@Composable
fun DescriptionField() {
    val todoFormVM = viewModel<TodoFormViewModel>()

    Column {
        Text(
            text = "描述",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.withBadge(
                badgeText = "*",
                textColor = MaterialTheme.colorScheme.error,
                offset = { Offset(size.width + 4.dp.toPx(), 8.dp.toPx()) }
            )
        )
        OutlinedTextField(
            value = todoFormVM.item.content,
            onValueChange = {
                todoFormVM.onValuesChange(todoFormVM.item.copy(content = it))
            },
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .heightIn(min = 160.dp),
            placeholder = { Text("请输入描述内容") },
            isError = todoFormVM.errorMap.containsKey("content"),
            supportingText = {
                if (todoFormVM.errorMap.containsKey("content")) {
                    Text(todoFormVM.errorMap.getValue("content"))
                }
            }
        )
    }
}
