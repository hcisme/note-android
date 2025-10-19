package io.github.hcisme.note.pages.todoform

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.DateTimePickerPopup
import io.github.hcisme.note.utils.LocalNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoFormPage(id: Long? = null) {
    val navHostController = LocalNavController.current
    val todoFormVM = viewModel<TodoFormViewModel>()
    val isEdit = remember(id) { id != null }
    var errors by remember { mutableStateOf(mapOf<String, String>()) }

    //
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEdit) "编辑待办事项" else "新增待办事项"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { todoFormVM.submit() }) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = Icons.Default.Done.name
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 标题输入
            OutlinedTextField(
                value = todoFormVM.item.title,
                onValueChange = {
                    todoFormVM.onValuesChange(todoFormVM.item.copy(title = it))
                },
                label = { Text("标题 *") },
                placeholder = { Text("请输入标题") },
                modifier = Modifier.fillMaxWidth(),
                isError = errors.containsKey("title"),
                supportingText = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 完成状态单选按钮
            Text(
                text = "完成状态 *",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    Text("未完成", modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = todoFormVM.item.completed == 1,
                        onClick = { todoFormVM.onValuesChange(todoFormVM.item.copy(completed = 1)) }
                    )
                    Text("已完成", modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 开始时间选择
            Text(
                text = "开始时间 *",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .onGloballyPositioned { coords ->
                        startTimeAnchorBoundsPx = coords.boundsInWindow()
                    }
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { startTimeVisible = true },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val time = todoFormVM.item.startTime
                Text(time.ifEmpty { "点击输入时间" })
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 结束时间选择
            Text(
                text = "结束时间 *",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .onGloballyPositioned { coords ->
                        endTimeAnchorBoundsPx = coords.boundsInWindow()
                    }
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { endTimeVisible = true },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val time = todoFormVM.item.endTime
                Text(time.ifEmpty { "点击输入时间" })
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 描述输入
            Text(
                text = "描述 *",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = todoFormVM.item.content,
                onValueChange = {
                    todoFormVM.onValuesChange(todoFormVM.item.copy(content = it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                placeholder = { Text("请输入描述内容") },
                isError = errors.containsKey("content"),
                supportingText = {}
            )
        }
    }

    startTimeAnchorBoundsPx?.let {
        DateTimePickerPopup(
            visible = startTimeVisible,
            anchorBoundsPx = it,
            onDismiss = { startTimeVisible = false }
        )
    }

    endTimeAnchorBoundsPx?.let {
        DateTimePickerPopup(
            visible = endTimeVisible,
            anchorBoundsPx = it,
            onDismiss = { endTimeVisible = false }
        )
    }
}
