package io.github.hcisme.note.pages.todoform

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.calcStatusBarHeight
import io.github.hcisme.note.components.time.DateTimePickerPopup
import io.github.hcisme.note.components.time.rememberTimePickerState
import io.github.hcisme.note.enums.FormFieldEnum
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
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.withBadge(
                badgeText = "*",
                textColor = MaterialTheme.colorScheme.error,
                offset = { Offset(size.width + 4.dp.toPx(), 8.dp.toPx()) }
            )
        )
        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .height(48.dp)
                .noRippleClickable { todoFormVM.currentEditField = FormFieldEnum.INPUT },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (todoFormVM.item.title.isNotEmpty()) {
                Text(todoFormVM.item.title)
            } else {
                Text(
                    text = "点击输入标题",
                    color = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.8.dp)
                .background(MaterialTheme.colorScheme.onBackground)
        )
        if (todoFormVM.errorMap.containsKey("title")) {
            Text(
                text = todoFormVM.errorMap.getValue("title"),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium
            )
        }
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
    val density = LocalDensity.current
    val timePickerState = rememberTimePickerState()
    val todoFormVM = viewModel<TodoFormViewModel>()
    // TODO 临时解决位置错误问题
    val statusBarHeight = calcStatusBarHeight()
    val topBarWithStatusBarHeightPx = remember(statusBarHeight) {
        with(density) { 80.dp.toPx() + statusBarHeight }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { timePickerState.coordsRef.set(it) },
    ) {
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
        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (todoFormVM.item.startTime.isNotEmpty()) {
                Text(todoFormVM.item.startTime)
            } else {
                Text(
                    text = "点击输入开始时间",
                    color = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }

            IconButton(onClick = { timePickerState.open() }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "选择开始日期",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.8.dp)
                .background(MaterialTheme.colorScheme.onBackground)
        )
        if (todoFormVM.errorMap.containsKey("startTime")) {
            Text(
                text = todoFormVM.errorMap.getValue("startTime"),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }

    DateTimePickerPopup(
        visible = timePickerState.visible,
        selectedDateTime = if (todoFormVM.item.startTime.isNotEmpty()) todoFormVM.item.startTime.toLocalDateTime() else null,
        anchorBoundsIntOffset = timePickerState.anchorOffset?.let { it.copy(y = it.y - topBarWithStatusBarHeightPx.toInt()) },
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
    val density = LocalDensity.current
    val timePickerState = rememberTimePickerState()
    val todoFormVM = viewModel<TodoFormViewModel>()
    // TODO 临时解决位置错误问题
    val statusBarHeight = calcStatusBarHeight()
    val topBarWithStatusBarHeightPx = remember(statusBarHeight) {
        with(density) { 68.dp.toPx() + statusBarHeight }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { timePickerState.coordsRef.set(it) }
    ) {
        Text(
            text = "结束时间",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (todoFormVM.item.endTime?.isNotEmpty() == true) {
                Text(todoFormVM.item.endTime!!)
            } else {
                Text(
                    text = "点击输入结束时间",
                    color = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (todoFormVM.item.endTime?.isNotEmpty() == true) {
                    IconButton(
                        onClick = {
                            todoFormVM.onValuesChange(todoFormVM.item.copy(endTime = null))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "清除结束日期",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = { timePickerState.open() }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "选择结束日期",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.8.dp)
                .background(MaterialTheme.colorScheme.onBackground)
        )
        if (todoFormVM.errorMap.containsKey("endTime")) {
            Text(
                text = todoFormVM.errorMap.getValue("endTime"),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }

    DateTimePickerPopup(
        visible = timePickerState.visible,
        selectedDateTime = todoFormVM.item.endTime?.toLocalDateTime(),
        anchorBoundsIntOffset = timePickerState.anchorOffset?.let { it.copy(y = it.y - topBarWithStatusBarHeightPx.toInt()) },
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
        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .noRippleClickable { todoFormVM.currentEditField = FormFieldEnum.TEXTAREA },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (todoFormVM.item.content.isNotEmpty()) {
                Text(todoFormVM.item.content)
            } else {
                Text(
                    text = "点击输入描述内容",
                    color = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        }
        if (todoFormVM.errorMap.containsKey("content")) {
            Text(
                text = todoFormVM.errorMap.getValue("content"),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
