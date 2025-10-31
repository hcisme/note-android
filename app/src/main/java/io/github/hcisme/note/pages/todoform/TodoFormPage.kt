package io.github.hcisme.note.pages.todoform

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.Dialog
import io.github.hcisme.note.components.keyboardHeightCalculator
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.noRippleClickable
import kotlinx.coroutines.launch

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

    fun backHandler() {
        if (keyboardHeight != 0.dp) {
            keyboardController?.hide()
            return
        }
        if (todoFormVM.haveChangedForm && !todoFormVM.backDialogVisible) {
            todoFormVM.backDialogVisible = true
            return
        }
        navHostController.popBackStack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TodoFormTopBar(isEdit = isEdit, onClickBackIcon = { backHandler() }) }
    ) { innerPadding ->
        val contentPadding = remember(keyboardHeight, innerPadding) {
            PaddingValues(
                start = innerPadding.calculateLeftPadding(LayoutDirection.Ltr),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateRightPadding(LayoutDirection.Ltr),
                bottom = if (keyboardHeight == 0.dp) innerPadding.calculateBottomPadding() else 0.dp
            )
        }

        Column(
            modifier = Modifier
                .padding(bottom = keyboardHeight)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .noRippleClickable { keyboardController?.hide() }
                .padding(contentPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(verticalScrollState),
        ) {
            // 标题输入
            TitleInputField()

            Spacer(modifier = Modifier.height(8.dp))

            // 完成状态单选按钮
            CompletionStatusField()

            Spacer(modifier = Modifier.height(8.dp))

            // 开始时间选择
            StartTimePickerField()

            Spacer(modifier = Modifier.height(8.dp))

            // 结束时间选择
            EndTimePickerField()

            Spacer(modifier = Modifier.height(8.dp))

            // 描述输入
            DescriptionField()
        }
    }

    Dialog(
        visible = todoFormVM.backDialogVisible,
        confirmButtonText = "确定",
        cancelButtonText = "取消",
        onConfirm = {
            scope.launch {
                todoFormVM.backDialogVisible = false
                focusManager.clearFocus()
                keyboardController?.hide()
                navHostController.popBackStack()
            }
        },
        onDismissRequest = { todoFormVM.backDialogVisible = false }
    ) {
        Text("编辑的内容未保存\n确认离开吗")
    }

    BackHandler { backHandler() }
}
