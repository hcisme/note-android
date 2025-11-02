package io.github.hcisme.note.pages.home.statistics

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.calcStatusBarHeight
import io.github.hcisme.note.components.time.DatePickerPopup
import io.github.hcisme.note.components.time.rememberTimePickerState
import io.github.hcisme.note.utils.DateUtil
import io.github.hcisme.note.utils.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticTopBar() {
    val context = LocalContext.current
    val statusBarHeight = calcStatusBarHeight()
    val datePickerState = rememberTimePickerState()
    val statisticsVM = viewModel<StatisticsViewModel>(context as ComponentActivity)
    val currentDate = statisticsVM.currentDate

    TopAppBar(
        title = {
            Column(
                modifier = Modifier
                    .noRippleClickable { datePickerState.open() }
                    .onGloballyPositioned { datePickerState.coordsRef.set(it) }
            ) {
                val month = remember(currentDate.monthNumber) {
                    if (currentDate.monthNumber in 1..12) "${DateUtil.months[currentDate.monthNumber - 1]} " else " "
                }
                Text(
                    text = "${month}${currentDate.year}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    )

    DatePickerPopup(
        visible = datePickerState.visible,
        anchorBoundsOffset = datePickerState.anchorOffset?.let { it.copy(y = it.y - statusBarHeight) },
        initialYear = currentDate.year,
        initialMonth = currentDate.monthNumber,
        allowUnSelectedMonth = true,
        onSelect = { year, month, _ ->
            if (year == currentDate.year && month == currentDate.monthNumber) return@DatePickerPopup
            statisticsVM.changeDate(
                SimpleDate(year = year, monthNumber = if (month in 1..12) month else 0)
            )
        },
        onDismiss = { datePickerState.close() }
    )
}
