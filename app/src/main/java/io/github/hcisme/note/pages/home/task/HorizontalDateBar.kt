package io.github.hcisme.note.pages.home.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.utils.DateUtil.shortWeekdays

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorizontalDateBar() {
    val taskVM: TaskViewModel = viewModel()
    val monthDates = taskVM.monthDates

    ScrollableTabRow(
        selectedTabIndex = taskVM.selectedTabIndex,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[taskVM.selectedTabIndex]),
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        monthDates.forEachIndexed { index, date ->
            Tab(
                selected = index == taskVM.selectedTabIndex,
                onClick = { taskVM.changeDate(index = index, date = date) },
                unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                selectedContentColor = MaterialTheme.colorScheme.primary
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = shortWeekdays[date.dayOfWeek.ordinal],
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(text = "${date.dayOfMonth}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
