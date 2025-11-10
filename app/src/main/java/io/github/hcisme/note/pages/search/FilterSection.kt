package io.github.hcisme.note.pages.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.enums.CompletionStatusEnum
import io.github.hcisme.note.enums.SortOrderEnum

@Composable
fun FilterSection() {
    val searchVM = viewModel<SearchViewModel>()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "筛选条件",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 时间筛选
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "时间:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.width(60.dp)
            )
            YearMonthInput(
                yearValue = searchVM.currentDate.year.toString(),
                monthValue = searchVM.currentDate.monthNumber.toString(),
                onYearChange = {
                    val newYear = if (it.isEmpty()) 0 else it.toInt()
                    searchVM.search(date = searchVM.currentDate.copy(year = newYear))
                },
                onMonthChange = {
                    val newMonth = if (it.isEmpty()) 0 else it.toInt()
                    searchVM.search(date = searchVM.currentDate.copy(monthNumber = newMonth))
                }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(top = 24.dp, bottom = 12.dp))

        // 排序方式
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "排序:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.width(60.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = searchVM.sortEnum == SortOrderEnum.ASC,
                    onClick = { searchVM.search(sortEnum = SortOrderEnum.ASC) },
                    label = { Text("正序") }
                )
                FilterChip(
                    selected = searchVM.sortEnum == SortOrderEnum.DESC,
                    onClick = { searchVM.search(sortEnum = SortOrderEnum.DESC) },
                    label = { Text("倒序") }
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // 完成状态
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "状态:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.width(60.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = searchVM.completedEnum == null,
                    onClick = { searchVM.search(completed = null) },
                    label = { Text("全部") }
                )
                FilterChip(
                    selected = searchVM.completedEnum == CompletionStatusEnum.COMPLETED,
                    onClick = { searchVM.search(completed = CompletionStatusEnum.COMPLETED) },
                    label = { Text("完成") }
                )
                FilterChip(
                    selected = searchVM.completedEnum == CompletionStatusEnum.INCOMPLETE,
                    onClick = { searchVM.search(completed = CompletionStatusEnum.INCOMPLETE) },
                    label = { Text("未完成") }
                )
            }
        }
    }
}
