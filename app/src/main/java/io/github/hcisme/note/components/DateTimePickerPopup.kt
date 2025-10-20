package io.github.hcisme.note.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.geometry.Rect
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
import io.github.hcisme.note.utils.formatWithPattern
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
    initialDateTime: LocalDateTime? = null,
    anchorBoundsPx: Rect,
    onDismiss: () -> Unit,
    onDateTimeSelected: (LocalDateTime) -> Unit = {}
) {
    val defaultTime =
        remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
    val xPx = anchorBoundsPx.left.toInt()
    val yPx = anchorBoundsPx.bottom.toInt()

    Popup(
        offset = IntOffset(x = xPx, y = yPx),
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            excludeFromSystemGesture = false
        )
    ) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = visible,
            enter = fadeIn(animationSpec = tween(160)),
            exit = fadeOut(animationSpec = tween(140))
        ) {
            DateTimePicker(
                initialDateTime = initialDateTime ?: defaultTime,
                onDateTimeChanged = onDateTimeSelected
            )
        }
    }
}

@Composable
fun DateTimePicker(
    modifier: Modifier = Modifier,
    /**
     * 只在初始化时使用一次
     */
    initialDateTime: LocalDateTime,
    minYear: Int = 1900,
    maxYear: Int = 2100,
    onDateTimeChanged: (LocalDateTime) -> Unit,
    itemHeight: Dp = 40.dp,
    visibleItemsCount: Int = 5,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    colors: DateTimePickerColors = DateTimePickerDefaults.colors()
) {
    var selectedDateTime by remember { mutableStateOf(initialDateTime) }

    val years = (minYear..maxYear).map { it.toString() }
    val months = (1..12).map { it.toString().padStart(2, '0') }
    val hours = (0..23).map { it.toString().padStart(2, '0') }
    val minutes = (0..59).map { it.toString().padStart(2, '0') }
    val seconds = (0..59).map { it.toString().padStart(2, '0') }

    val daysInMonth =
        if (selectedDateTime.year % 4 == 0 && (selectedDateTime.year % 100 != 0 || selectedDateTime.year % 400 == 0)) {
            when (selectedDateTime.monthNumber) {
                2 -> 29 // 闰年二月
                4, 6, 9, 11 -> 30
                else -> 31
            }
        } else {
            when (selectedDateTime.monthNumber) {
                2 -> 28 // 平年二月
                4, 6, 9, 11 -> 30
                else -> 31
            }
        }
    val days = (1..daysInMonth).map { it.toString().padStart(2, '0') }

    fun update(newDateTime: LocalDateTime) {
        // 只有当值真正改变时才更新，防止不必要的重组
        if (newDateTime.formatWithPattern() != selectedDateTime.formatWithPattern()) {
            selectedDateTime = newDateTime
            // 使用防抖机制，避免频繁回调
            onDateTimeChanged(newDateTime)
            println("DateTimePicker: 时间已更新为 $newDateTime")
        }
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
            modifier = Modifier.weight(1.5f), // 年份通常需要更宽
            items = years,
            initialIndex = years.indexOf(selectedDateTime.year.toString()).coerceAtLeast(0),
            itemHeight = itemHeight,
            visibleItemsCount = visibleItemsCount,
            textStyle = textStyle,
            selectedTextColor = colors.selectedTextColor,
            unselectedTextColor = colors.unselectedTextColor,
            onValueChange = { index ->
                val newYear = years[index].toInt()
                val oldDay = selectedDateTime.dayOfMonth
                var newDateTime = LocalDateTime(
                    year = newYear,
                    monthNumber = selectedDateTime.monthNumber,
                    dayOfMonth = oldDay,
                    hour = selectedDateTime.hour,
                    minute = selectedDateTime.minute,
                    second = selectedDateTime.second
                )
                val maxDaysInNewMonth =
                    if (newYear % 4 == 0 && (newYear % 100 != 0 || newYear % 400 == 0)) {
                        when (selectedDateTime.monthNumber) {
                            2 -> 29
                            4, 6, 9, 11 -> 30
                            else -> 31
                        }
                    } else {
                        when (selectedDateTime.monthNumber) {
                            2 -> 28
                            4, 6, 9, 11 -> 30
                            else -> 31
                        }
                    }
                if (oldDay > maxDaysInNewMonth) {
                    newDateTime = LocalDateTime(
                        year = newYear,
                        monthNumber = selectedDateTime.monthNumber,
                        dayOfMonth = maxDaysInNewMonth,
                        hour = selectedDateTime.hour,
                        minute = selectedDateTime.minute,
                        second = selectedDateTime.second
                    )
                }
                update(newDateTime)
            }
        )
        Text(
            "年",
            style = textStyle,
            color = colors.unselectedTextColor,
            modifier = Modifier.width(25.dp)
        )

        WheelPicker(
            modifier = Modifier.weight(1f),
            items = months,
            initialIndex = selectedDateTime.monthNumber - 1,
            itemHeight = itemHeight,
            visibleItemsCount = visibleItemsCount,
            textStyle = textStyle,
            selectedTextColor = colors.selectedTextColor,
            unselectedTextColor = colors.unselectedTextColor,
            onValueChange = { index ->
                val newMonth = months[index].toInt()
                val oldDay = selectedDateTime.dayOfMonth
                var newDateTime = LocalDateTime(
                    year = selectedDateTime.year,
                    monthNumber = newMonth,
                    dayOfMonth = oldDay,
                    hour = selectedDateTime.hour,
                    minute = selectedDateTime.minute,
                    second = selectedDateTime.second
                )
                val maxDaysInNewMonth =
                    if (selectedDateTime.year % 4 == 0 && (selectedDateTime.year % 100 != 0 || selectedDateTime.year % 400 == 0)) {
                        when (newMonth) {
                            2 -> 29
                            4, 6, 9, 11 -> 30
                            else -> 31
                        }
                    } else {
                        when (newMonth) {
                            2 -> 28
                            4, 6, 9, 11 -> 30
                            else -> 31
                        }
                    }
                if (oldDay > maxDaysInNewMonth) {
                    newDateTime = LocalDateTime(
                        year = selectedDateTime.year,
                        monthNumber = newMonth,
                        dayOfMonth = maxDaysInNewMonth,
                        hour = selectedDateTime.hour,
                        minute = selectedDateTime.minute,
                        second = selectedDateTime.second
                    )
                }
                update(newDateTime)
            }
        )
        Text(
            "月",
            style = textStyle,
            color = colors.unselectedTextColor,
            modifier = Modifier.width(25.dp)
        )

        // 使用 key 来确保在列表变化时重置状态
        key(days.size) {
            WheelPicker(
                modifier = Modifier.weight(1f),
                items = days,
                initialIndex = (selectedDateTime.dayOfMonth - 1).coerceIn(0, days.size - 1),
                itemHeight = itemHeight,
                visibleItemsCount = visibleItemsCount,
                textStyle = textStyle,
                selectedTextColor = colors.selectedTextColor,
                unselectedTextColor = colors.unselectedTextColor,
                onValueChange = { index ->
                    val newDay = days[index].toInt()
                    update(
                        LocalDateTime(
                            year = selectedDateTime.year,
                            monthNumber = selectedDateTime.monthNumber,
                            dayOfMonth = newDay,
                            hour = selectedDateTime.hour,
                            minute = selectedDateTime.minute,
                            second = selectedDateTime.second
                        )
                    )
                }
            )
        }
        Text(
            "日",
            style = textStyle,
            color = colors.unselectedTextColor,
            modifier = Modifier.width(25.dp)
        )

        WheelPicker(
            modifier = Modifier.weight(1f),
            items = hours,
            initialIndex = selectedDateTime.hour,
            itemHeight = itemHeight,
            visibleItemsCount = visibleItemsCount,
            textStyle = textStyle,
            selectedTextColor = colors.selectedTextColor,
            unselectedTextColor = colors.unselectedTextColor,
            onValueChange = { index ->
                val newHour = hours[index].toInt()
                update(
                    LocalDateTime(
                        year = selectedDateTime.year,
                        monthNumber = selectedDateTime.monthNumber,
                        dayOfMonth = selectedDateTime.dayOfMonth,
                        hour = newHour,
                        minute = selectedDateTime.minute,
                        second = selectedDateTime.second
                    )
                )
            }
        )
        Text(
            "时",
            style = textStyle,
            color = colors.unselectedTextColor,
            modifier = Modifier.width(25.dp)
        )

        WheelPicker(
            modifier = Modifier.weight(1f),
            items = minutes,
            initialIndex = selectedDateTime.minute,
            itemHeight = itemHeight,
            visibleItemsCount = visibleItemsCount,
            textStyle = textStyle,
            selectedTextColor = colors.selectedTextColor,
            unselectedTextColor = colors.unselectedTextColor,
            onValueChange = { index ->
                val newMinute = minutes[index].toInt()
                // 更改15: 创建新的 LocalDateTime
                update(
                    LocalDateTime(
                        year = selectedDateTime.year,
                        monthNumber = selectedDateTime.monthNumber,
                        dayOfMonth = selectedDateTime.dayOfMonth,
                        hour = selectedDateTime.hour,
                        minute = newMinute,
                        second = selectedDateTime.second
                    )
                )
            }
        )
        Text(
            "分",
            style = textStyle,
            color = colors.unselectedTextColor,
            modifier = Modifier.width(25.dp)
        )

        WheelPicker(
            modifier = Modifier.weight(1f),
            items = seconds,
            initialIndex = selectedDateTime.second,
            itemHeight = itemHeight,
            visibleItemsCount = visibleItemsCount,
            textStyle = textStyle,
            selectedTextColor = colors.selectedTextColor,
            unselectedTextColor = colors.unselectedTextColor,
            onValueChange = { index ->
                val newSecond = seconds[index].toInt()
                update(
                    LocalDateTime(
                        year = selectedDateTime.year,
                        monthNumber = selectedDateTime.monthNumber,
                        dayOfMonth = selectedDateTime.dayOfMonth,
                        hour = selectedDateTime.hour,
                        minute = selectedDateTime.minute,
                        second = newSecond
                    )
                )
            }
        )
        Text(
            "秒",
            style = textStyle,
            color = colors.unselectedTextColor,
            modifier = Modifier.width(25.dp)
        )
    }
}

object DateTimePickerDefaults {
    @Composable
    fun colors(
        selectedTextColor: Color = MaterialTheme.colorScheme.primary,
        unselectedTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        dividerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    ): DateTimePickerColors = DateTimePickerColors(
        selectedTextColor = selectedTextColor,
        unselectedTextColor = unselectedTextColor,
        dividerColor = dividerColor
    )
}

data class DateTimePickerColors(
    val selectedTextColor: Color,
    val unselectedTextColor: Color,
    val dividerColor: Color
)


@Composable
private fun WheelPicker(
    modifier: Modifier = Modifier,
    items: List<String>,
    initialIndex: Int = 0,
    visibleItemsCount: Int = 5,
    itemHeight: Dp = 40.dp,
    textStyle: TextStyle,
    selectedTextColor: Color,
    unselectedTextColor: Color,
    showDividers: Boolean = true, // 新增：控制分割线是否显示
    dividerColor: Color = selectedTextColor.copy(alpha = 0.3f), // 新增：分割线颜色
    onValueChange: (index: Int) -> Unit,
) {
    val correctedVisibleItemsCount =
        if (visibleItemsCount % 2 == 0) visibleItemsCount + 1 else visibleItemsCount

    // 关键修正：确保 initialIndex 不会越界
    val safeInitialIndex = initialIndex.coerceIn(0, items.size - 1)
    val lazyListState = rememberLazyListState(safeInitialIndex)
    val density = LocalDensity.current

    // 使用 SnapFlingBehavior
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState)

    // 防止外部更新和用户滚动冲突的标记
    var isInternalUpdate by remember { mutableStateOf(false) }

    // 计算当前选中的索引（只在滚动停止时计算，避免频繁更新）
    val currentSelectedIndex by remember {
        derivedStateOf {
            if (lazyListState.isScrollInProgress) {
                // 滚动过程中不计算，使用上一次的值
                return@derivedStateOf lazyListState.firstVisibleItemIndex.coerceIn(
                    0,
                    items.size - 1
                )
            }

            val itemHeightPx = with(density) { itemHeight.toPx() }
            if (lazyListState.firstVisibleItemScrollOffset > itemHeightPx / 2) {
                (lazyListState.firstVisibleItemIndex + 1).coerceIn(0, items.size - 1)
            } else {
                lazyListState.firstVisibleItemIndex.coerceIn(0, items.size - 1)
            }
        }
    }

    // 只在滚动停止时触发回调，添加防抖机制
    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (!lazyListState.isScrollInProgress && !isInternalUpdate) {
            // 添加小延迟，确保滚动完全停止
            delay(50)

            val itemHeightPx = with(density) { itemHeight.toPx() }
            val finalIndex = if (lazyListState.firstVisibleItemScrollOffset > itemHeightPx / 2) {
                (lazyListState.firstVisibleItemIndex + 1).coerceIn(0, items.size - 1)
            } else {
                lazyListState.firstVisibleItemIndex.coerceIn(0, items.size - 1)
            }
            println(
                "WheelPicker: 滚动停止，选中索引: $finalIndex, 对应值: ${
                    items.getOrNull(
                        finalIndex
                    )
                }"
            )
            onValueChange(finalIndex)
        }
    }

    // 当外部的 initialIndex 变化时，让列表滚动到新位置
    LaunchedEffect(safeInitialIndex) {
        if (!lazyListState.isScrollInProgress) {
            isInternalUpdate = true
            lazyListState.animateScrollToItem(safeInitialIndex)
            isInternalUpdate = false
        }
    }

    Box(
        modifier = modifier.height(itemHeight * correctedVisibleItemsCount),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .height(itemHeight * correctedVisibleItemsCount)
                .width(200.dp), // 固定宽度
            contentPadding = PaddingValues(vertical = itemHeight * ((correctedVisibleItemsCount - 1) / 2)),
            flingBehavior = snapFlingBehavior // 使用napFlingBehavior
        ) {
            items(items.size) { index ->
                // 使用更稳定的中心计算方式
                val centerIndex = remember(
                    lazyListState.firstVisibleItemIndex,
                    lazyListState.firstVisibleItemScrollOffset
                ) {
                    derivedStateOf {
                        val itemHeightPx = with(density) { itemHeight.toPx() }
                        if (lazyListState.firstVisibleItemScrollOffset > itemHeightPx / 2) {
                            (lazyListState.firstVisibleItemIndex + 1).coerceIn(0, items.size - 1)
                        } else {
                            lazyListState.firstVisibleItemIndex.coerceIn(0, items.size - 1)
                        }
                    }
                }.value

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

        // 分割线（可选显示）
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
