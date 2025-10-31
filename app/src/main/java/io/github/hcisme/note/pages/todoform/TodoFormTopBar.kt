package io.github.hcisme.note.pages.todoform

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.R
import io.github.hcisme.note.components.RotationIcon
import io.github.hcisme.note.utils.withBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoFormTopBar(isEdit: Boolean, onClickBackIcon: () -> Unit = {}) {
    val todoFormVM = viewModel<TodoFormViewModel>()

    TopAppBar(
        title = { Text(text = if (isEdit) "编辑事项" else "新增事项") },
        navigationIcon = {
            IconButton(onClick = { onClickBackIcon() }) {
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
