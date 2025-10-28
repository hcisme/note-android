package io.github.hcisme.note.components

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.github.hcisme.note.utils.copy
import io.github.hcisme.note.utils.formatWithPattern
import io.github.hcisme.note.utils.getMaxDaysInMonth
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DateTimePickerPopup(
    visible: Boolean,
    selectedDateTime: LocalDateTime? = null,
    anchorBoundsIntOffset: IntOffset? = null,
    onDismiss: () -> Unit,
    onDateTimeSelected: (LocalDateTime) -> Unit = {}
) {
    val defaultTime =
        remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }

    if (visible && anchorBoundsIntOffset != null) {
        Popup(
            offset = anchorBoundsIntOffset,
            onDismissRequest = onDismiss,
            properties = PopupProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                excludeFromSystemGesture = false
            )
        ) {
            DateTimePicker(
                selectedDateTime = selectedDateTime ?: defaultTime,
                onDateTimeChanged = onDateTimeSelected
            )
        }
    }
}

@Composable
fun DateTimePicker(
    modifier: Modifier = Modifier,
    selectedDateTime: LocalDateTime,
    minYear: Int = 1900,
    maxYear: Int = 2100,
    itemHeight: Dp = 40.dp,
    visibleItemsCount: Int = 5,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    onDateTimeChanged: (LocalDateTime) -> Unit,
) {
    val daysInMonth = remember(selectedDateTime.year, selectedDateTime.monthNumber) {
        selectedDateTime.getMaxDaysInMonth()
    }
    val years = remember(minYear, maxYear) { (minYear..maxYear).map { it.toString() } }
    val months = remember { (1..12).map { it.toString().padStart(2, '0') } }
    val days = remember(daysInMonth) { (1..daysInMonth).map { it.toString().padStart(2, '0') } }
    val hours = remember { (0..23).map { it.toString().padStart(2, '0') } }
    val minutes = remember { (0..59).map { it.toString().padStart(2, '0') } }
    val seconds = remember { (0..59).map { it.toString().padStart(2, '0') } }

    fun update(newDateTime: LocalDateTime) {
        onDateTimeChanged(newDateTime)
        Log.d("DateTimePicker", "时间已更新为 ${newDateTime.formatWithPattern()}")
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WheelPicker(
            modifier = Modifier.weight(1.5f),
            items = years,
            selectedIndex = years.indexOf(selectedDateTime.year.toString()).coerceAtLeast(0),
            itemHeight = itemHeight,
            visibleItemsCount = visibleItemsCount,
            textStyle = textStyle,
            onValueChange = { index ->
                val newYear = years[index].toInt()
                update(selectedDateTime.copy(year = newYear))
            }
        )
        Text(
            "年",
            style = textStyle,
            modifier = Modifier.width(25.dp)
        )

        WheelPicker(
            modifier = Modifier.weight(1f),
            items = months,
            selectedIndex = selectedDateTime.monthNumber - 1,
            itemHeight = itemHeight,
            visibleItemsCount = visibleItemsCount,
            textStyle = textStyle,
            onValueChange = { index ->
                val newMonth = months[index].toInt()
                update(selectedDateTime.copy(month = newMonth))
            }
        )
        Text(
            "月",
            style = textStyle,
            modifier = Modifier.width(25.dp)
        )

        // 使用 key 来确保在列表变化时重置状态
        key(days.size) {
            WheelPicker(
                modifier = Modifier.weight(1f),
                items = days,
                selectedIndex = (selectedDateTime.dayOfMonth - 1).coerceIn(0, days.size - 1),
                itemHeight = itemHeight,
                visibleItemsCount = visibleItemsCount,
                textStyle = textStyle,
                onValueChange = { index ->
                    val newDay = days[index].toInt()
                    update(selectedDateTime.copy(day = newDay))
                }
            )
        }
        Text(
            "日",
            style = textStyle,
            modifier = Modifier.width(25.dp)
        )

        WheelPicker(
            modifier = Modifier.weight(1f),
            items = hours,
            selectedIndex = selectedDateTime.hour,
            itemHeight = itemHeight,
            visibleItemsCount = visibleItemsCount,
            textStyle = textStyle,
            onValueChange = { index ->
                val newHour = hours[index].toInt()
                update(selectedDateTime.copy(hour = newHour))
            }
        )
        Text(
            "时",
            style = textStyle,
            modifier = Modifier.width(25.dp)
        )

        WheelPicker(
            modifier = Modifier.weight(1f),
            items = minutes,
            selectedIndex = selectedDateTime.minute,
            itemHeight = itemHeight,
            visibleItemsCount = visibleItemsCount,
            textStyle = textStyle,
            onValueChange = { index ->
                val newMinute = minutes[index].toInt()
                update(selectedDateTime.copy(minute = newMinute))
            }
        )
        Text(
            "分",
            style = textStyle,
            modifier = Modifier.width(25.dp)
        )

        WheelPicker(
            modifier = Modifier.weight(1f),
            items = seconds,
            selectedIndex = selectedDateTime.second,
            itemHeight = itemHeight,
            visibleItemsCount = visibleItemsCount,
            textStyle = textStyle,
            onValueChange = { index ->
                val newSecond = seconds[index].toInt()
                update(selectedDateTime.copy(second = newSecond))
            }
        )
        Text(
            "秒",
            style = textStyle,
            modifier = Modifier.width(25.dp)
        )
    }
}

@Composable
fun WheelPicker(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedIndex: Int = 0,
    visibleItemsCount: Int = 5,
    itemHeight: Dp = 40.dp,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    selectedTextColor: Color = MaterialTheme.colorScheme.primary,
    unselectedTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
    showDividers: Boolean = true,
    dividerColor: Color = selectedTextColor.copy(alpha = 0.3f),
    onValueChange: (index: Int) -> Unit,
) {
    val density = LocalDensity.current
    val safeSelectedIndex = remember(selectedIndex) { selectedIndex.coerceIn(0, items.size - 1) }
    val lazyListState = rememberLazyListState(safeSelectedIndex)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState)
    val correctedVisibleItemsCount = remember(visibleItemsCount) {
        if (visibleItemsCount % 2 == 0) visibleItemsCount + 1 else visibleItemsCount
    }
    val itemHeightPx = remember(density, itemHeight) { with(density) { itemHeight.toPx() } }
    // 防止外部更新和用户滚动冲突的标记
    var isInternalUpdate by remember { mutableStateOf(false) }
    val centerIndex by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemScrollOffset > itemHeightPx / 2) {
                (lazyListState.firstVisibleItemIndex + 1).coerceIn(0, items.size - 1)
            } else {
                lazyListState.firstVisibleItemIndex.coerceIn(0, items.size - 1)
            }
        }
    }

    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (!lazyListState.isScrollInProgress && !isInternalUpdate) {
            delay(50)

            val finalIndex = if (lazyListState.firstVisibleItemScrollOffset > itemHeightPx / 2) {
                (lazyListState.firstVisibleItemIndex + 1).coerceIn(0, items.size - 1)
            } else {
                lazyListState.firstVisibleItemIndex.coerceIn(0, items.size - 1)
            }
            if (finalIndex != safeSelectedIndex) {
                Log.d(
                    "WheelPicker",
                    "滚动停止，选中索引: $finalIndex, 对应值: ${items.getOrNull(finalIndex)}"
                )
                onValueChange(finalIndex)
            }
        }
    }

    LaunchedEffect(safeSelectedIndex) {
        if (!lazyListState.isScrollInProgress) {
            isInternalUpdate = true
            lazyListState.animateScrollToItem(safeSelectedIndex)
            isInternalUpdate = false
        }
    }

    Box(
        modifier = modifier
            .height(itemHeight * correctedVisibleItemsCount),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight(),
            contentPadding = PaddingValues(vertical = itemHeight * ((correctedVisibleItemsCount - 1) / 2)),
            flingBehavior = snapFlingBehavior
        ) {
            items(items.size) { index ->
                // 计算距离中心的距离
                val distanceFromCenter = abs(index - centerIndex)

                // 计算透明度和缩放
                val alpha = when {
                    distanceFromCenter == 0 -> 1f
                    distanceFromCenter <= correctedVisibleItemsCount / 2 -> 1f - (distanceFromCenter.toFloat() / (correctedVisibleItemsCount / 2)) * 0.6f
                    else -> 0.2f
                }

                val scale = when {
                    distanceFromCenter == 0 -> 1f
                    distanceFromCenter <= correctedVisibleItemsCount / 2 -> 1f - (distanceFromCenter.toFloat() / (correctedVisibleItemsCount / 2)) * 0.3f
                    else -> 0.7f
                }

                // 计算旋转角度
                val rotationX =
                    -20f * distanceFromCenter.toFloat() / (correctedVisibleItemsCount / 2)

                val isSelected = index == centerIndex

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .width(200.dp)
                        .alpha(alpha)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.rotationX = rotationX.coerceIn(-20f, 20f)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items.getOrElse(index) { "" },
                        style = textStyle,
                        color = if (isSelected) selectedTextColor else unselectedTextColor,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        // 分割线
        if (showDividers) {
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .width(200.dp)
                    .alpha(0.6f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalDivider(thickness = 1.dp, color = dividerColor)
                    Spacer(modifier = Modifier.height(itemHeight - 2.dp))
                    HorizontalDivider(thickness = 1.dp, color = dividerColor)
                }
            }
        }
    }
}
