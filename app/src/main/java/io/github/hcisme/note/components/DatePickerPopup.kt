package io.github.hcisme.note.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import io.github.hcisme.note.utils.DateUtil
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun DatePickerPopup(
    visible: Boolean,
    anchorBoundsPx: Rect,
    initialYear: Int,
    initialMonth: Int,
    spanEachSide: Int = 5,
    onSelect: (year: Int, month: Int, day: Int) -> Unit,
    onClickToday: (year: Int, month: Int, day: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val yearListState = rememberLazyListState()
    val monthListState = rememberLazyListState()
    var selectedYear by remember { mutableIntStateOf(initialYear) }
    var selectedMonth by remember { mutableIntStateOf(initialMonth) }
    val years = remember(initialYear) { DateUtil.yearsAround(initialYear, spanEachSide) }
    val months = remember { (1..12).toList() }
    // Popup 的目标偏移（基于 anchor 的左下角）
    val xPx = anchorBoundsPx.left.toInt()
    val yPx = anchorBoundsPx.bottom.toInt()

    LaunchedEffect(visible) {
        if (visible) {
            val yearIndex = years.indexOfFirst { it == selectedYear }.let { if (it >= 0) it else 0 }
            val monthIndex =
                months.indexOfFirst { it == selectedMonth }.let { if (it >= 0) it else 0 }
            yearListState.scrollToItem(yearIndex)
            monthListState.scrollToItem(monthIndex)
        }
    }

    Popup(onDismissRequest = onDismiss, offset = IntOffset(x = xPx, y = yPx)) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(160)),
            exit = fadeOut(animationSpec = tween(140))
        ) {
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "选择年份",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        modifier = Modifier.padding(vertical = 4.dp),
                        state = yearListState
                    ) {
                        items(years) { y ->
                            val selected = y == selectedYear
                            Surface(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable { selectedYear = y },
                                shape = RoundedCornerShape(8.dp),
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                tonalElevation = if (selected) 4.dp else 0.dp
                            ) {
                                Text(
                                    text = "$y",
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 8.dp
                                    ),
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "选择月份",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        modifier = Modifier.padding(vertical = 4.dp),
                        state = monthListState
                    ) {
                        items(months) { m ->
                            val selected = m == selectedMonth

                            Surface(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable { selectedMonth = m },
                                shape = RoundedCornerShape(8.dp),
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                tonalElevation = if (selected) 4.dp else 0.dp
                            ) {
                                Text(
                                    text = "${m}月",
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 8.dp
                                    ),
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        TextButton(
                            onClick = {
                                val today =
                                    Clock.System.now()
                                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                                onClickToday(today.year, today.monthNumber, today.dayOfMonth)
                                onDismiss()
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("今天")
                        }

                        TextButton(
                            onClick = {
                                onSelect(selectedYear, selectedMonth, 1)
                                onDismiss()
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("确定")
                        }


                    }
                }
            }
        }
    }
}

