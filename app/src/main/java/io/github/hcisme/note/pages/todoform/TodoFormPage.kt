package io.github.hcisme.note.pages.todoform

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.R
import io.github.hcisme.note.components.DateTimePickerPopup
import io.github.hcisme.note.components.Dialog
import io.github.hcisme.note.components.RotationIcon
import io.github.hcisme.note.components.keyboardHeightCalculator
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.formatWithPattern
import io.github.hcisme.note.utils.noRippleClickable
import io.github.hcisme.note.utils.toLocalDateTime
import io.github.hcisme.note.utils.withBadge
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoFormPage(id: Long? = null) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val navHostController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val verticalScrollState = rememberScrollState()
    val keyboardHeight = keyboardHeightCalculator()
    val todoFormVM = viewModel<TodoFormViewModel>()
    val isEdit = remember(id) { id != null }

    // 操控 ui 相关变量
    var dialogVisible by remember { mutableStateOf(false) }
    var startTimeVisible by remember { mutableStateOf(false) }
    var startTimeAnchorBoundsPx by remember { mutableStateOf<Rect?>(null) }
    var endTimeVisible by remember { mutableStateOf(false) }
    var endTimeAnchorBoundsPx by remember { mutableStateOf<Rect?>(null) }

    LaunchedEffect(id) {
        if (id != null) {
            todoFormVM.item = todoFormVM.item.copy(id = id)
            todoFormVM.getTodoItem(id)
        }
    }

    LaunchedEffect(keyboardHeight) {
        if (keyboardHeight == 0.dp) {
            focusManager.clearFocus()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = if (isEdit) "编辑事项" else "新增事项") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (todoFormVM.haveChangedForm) {
                                dialogVisible = true
                            } else {
                                scope.launch {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    delay(300)
                                    navHostController.popBackStack()
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                        )
                    }
                },
                actions = {
                    val color = MaterialTheme.colorScheme.primary
                    IconButton(
                        modifier = Modifier.withBadge(
                            showBadge = todoFormVM.haveChangedForm,
                            badgeColor = color,
                            offset = { Offset(size.width - 12.dp.toPx(), 12.dp.toPx()) }
                        ),
                        onClick = { todoFormVM.submit() }
                    ) {
                        if (todoFormVM.loading) {
                            RotationIcon(painter = painterResource(R.drawable.loading_circle))
                        } else {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = Icons.Default.Done.name,
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val contentPadding = PaddingValues(
            start = innerPadding.calculateLeftPadding(LayoutDirection.Ltr),
            top = innerPadding.calculateTopPadding(),
            end = innerPadding.calculateRightPadding(LayoutDirection.Ltr),
            bottom = 0.dp
        )

        Column(
            modifier = Modifier
                .padding(bottom = keyboardHeight)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .noRippleClickable {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
                .padding(contentPadding)
                .padding(16.dp)
                .verticalScroll(verticalScrollState),
        ) {
            // 标题输入
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
                isError = todoFormVM.errorMap.containsKey("title"),
                supportingText = {
                    if (todoFormVM.errorMap.containsKey("title")) {
                        Text(todoFormVM.errorMap.getValue("title"))
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 完成状态单选按钮
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

            Spacer(modifier = Modifier.height(16.dp))

            // 开始时间选择
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
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(
                        width = 1.dp,
                        color = if (todoFormVM.errorMap.containsKey("startTime")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .onGloballyPositioned { coords ->
                        startTimeAnchorBoundsPx = coords.boundsInWindow()
                    }
                    .noRippleClickable { startTimeVisible = true }
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val time = todoFormVM.item.startTime
                Text(time.ifEmpty { "点击输入时间" })
            }
            if (todoFormVM.errorMap.containsKey("startTime")) {
                Text(
                    text = todoFormVM.errorMap.getValue("startTime"),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 结束时间选择
            Text(
                text = "结束时间",
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
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(
                        width = 1.dp,
                        color = if (todoFormVM.errorMap.containsKey("endTime")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .onGloballyPositioned { coords ->
                        endTimeAnchorBoundsPx = coords.boundsInWindow()
                    }
                    .noRippleClickable { endTimeVisible = true }
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val time = todoFormVM.item.endTime
                Text(time.ifEmpty { "点击输入时间" })
            }
            if (todoFormVM.errorMap.containsKey("endTime")) {
                Text(
                    text = todoFormVM.errorMap.getValue("endTime"),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 描述输入
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
                    .height(160.dp),
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

    startTimeAnchorBoundsPx?.let {
        DateTimePickerPopup(
            visible = startTimeVisible,
            initialDateTime = if (todoFormVM.item.startTime.isNotEmpty()) todoFormVM.item.startTime.toLocalDateTime() else null,
            anchorBoundsPx = it,
            onDismiss = {
                startTimeVisible = false
                startTimeAnchorBoundsPx = null
            },
            onDateTimeSelected = { dateTime ->
                todoFormVM.onValuesChange(todoFormVM.item.copy(startTime = dateTime.formatWithPattern()))
            }
        )
    }

    endTimeAnchorBoundsPx?.let {
        DateTimePickerPopup(
            visible = endTimeVisible,
            initialDateTime = if (todoFormVM.item.endTime.isNotEmpty()) todoFormVM.item.endTime.toLocalDateTime() else null,
            anchorBoundsPx = it,
            onDismiss = {
                endTimeVisible = false
                endTimeAnchorBoundsPx = null
            },
            onDateTimeSelected = { dateTime ->
                todoFormVM.onValuesChange(todoFormVM.item.copy(endTime = dateTime.formatWithPattern()))
            }
        )
    }

    Dialog(
        visible = dialogVisible,
        confirmButtonText = "确定",
        cancelButtonText = "取消",
        onConfirm = {
            scope.launch {
                dialogVisible = false
                focusManager.clearFocus()
                keyboardController?.hide()
                delay(300)
                navHostController.popBackStack()
            }
        },
        onDismissRequest = { dialogVisible = false },
    ) {
        Text("编辑的内容未保存\n确认离开吗")
    }

    BackHandler(
        enabled = todoFormVM.haveChangedForm
                || startTimeVisible
                || endTimeVisible
    ) {
        if (!dialogVisible) {
            dialogVisible = true
        }

        if (startTimeVisible || endTimeVisible) {
            startTimeVisible = false
            endTimeVisible = false
        }
    }
}
