package io.github.hcisme.note.pages.home.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.time.DatePickerPopup
import io.github.hcisme.note.navigation.navigateToTodoForm
import io.github.hcisme.note.components.time.rememberTimePickerState
import io.github.hcisme.note.utils.DateUtil
import io.github.hcisme.note.utils.DateUtil.months
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.noRippleClickable
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTaskTopBar() {
    val navHostController = LocalNavController.current
    val taskVM = viewModel<TaskViewModel>()
    val currentDate = taskVM.currentDate
    val datePickerState = rememberTimePickerState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(9_000)
            if (taskVM.isToday && taskVM.today != taskVM.currentDate) {
                val days = DateUtil.getMaxDaysInMonth(
                    year = taskVM.currentDate.year,
                    month = taskVM.currentDate.monthNumber
                )
                taskVM.changeDate(
                    index = if (days == taskVM.selectedTabIndex + 1) 0 else taskVM.selectedTabIndex + 1,
                    date = taskVM.today
                )
            }
        }
    }


    TopAppBar(
        title = {
            Column(
                modifier = Modifier
                    .noRippleClickable { datePickerState.open() }
                    .onGloballyPositioned { datePickerState.coordsRef.set(it) }
            ) {
                Text(
                    text = "${months[currentDate.monthNumber - 1]} ${currentDate.year}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = if (taskVM.isToday) 0.4f else 1f)
                )
                if (taskVM.isToday) {
                    Text(
                        text = "今天",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        actions = {
            FilledTonalButton(
                onClick = { navHostController.navigateToTodoForm() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add"
                )
                Text(text = "添加任务")
            }
        }
    )

    DatePickerPopup(
        visible = datePickerState.visible,
        anchorBoundsOffset = datePickerState.anchorOffset,
        initialYear = currentDate.year,
        initialMonth = currentDate.monthNumber,
        onSelect = { year, month, day ->
            if (year == currentDate.year && month == currentDate.monthNumber) return@DatePickerPopup
            taskVM.changeDate(
                index = day - 1,
                date = LocalDate(year = year, monthNumber = month, dayOfMonth = day)
            )
        },
        onClickToday = { year, month, day ->
            taskVM.changeDate(
                index = day - 1,
                date = LocalDate(year = year, monthNumber = month, dayOfMonth = day)
            )
        },
        onDismiss = { datePickerState.close() }
    )
}
