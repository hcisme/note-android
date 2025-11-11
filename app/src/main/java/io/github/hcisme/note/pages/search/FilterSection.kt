package io.github.hcisme.note.pages.search

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.R
import io.github.hcisme.note.components.Tag
import io.github.hcisme.note.enums.CompletionStatusEnum
import io.github.hcisme.note.enums.SortOrderEnum
import io.github.hcisme.note.utils.noRippleClickable

@Composable
fun FilterSection() {
    val searchVM = viewModel<SearchViewModel>()
    var filterDialogVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Tag("时间: ${searchVM.currentDate.year}-${searchVM.currentDate.monthNumber}")
                searchVM.sortEnum?.let { Tag("排序: ${it.message}") }
                Tag("状态: ${searchVM.completedEnum?.desc ?: "全部"}")
            }

            Icon(
                painter = painterResource(R.drawable.filter),
                contentDescription = "过滤",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.noRippleClickable {
                    filterDialogVisible = !filterDialogVisible
                }
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
        ) {
            if (filterDialogVisible) {
                EditableFilterContent()
            }
        }

    }
}

@Composable
private fun EditableFilterContent() {
    val searchVM = viewModel<SearchViewModel>()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 时间筛选
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "时间:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
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
                color = MaterialTheme.colorScheme.onBackground,
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
                color = MaterialTheme.colorScheme.onBackground,
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
