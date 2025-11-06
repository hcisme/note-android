package io.github.hcisme.note.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.hcisme.note.enums.CompletionStatusEnum
import io.github.hcisme.note.network.model.TodoItemModel
import io.github.hcisme.note.utils.noRippleClickable

@Composable
fun TimelineTaskItem(
    modifier: Modifier = Modifier,
    item: TodoItemModel,
    isCurrent: Boolean,
    isLast: Boolean,
    height: Dp = 200.dp,
    contentMaxLines: Int = 3,
    onClick: () -> Unit = {},
    onClickDelete: () -> Unit = {}
) {
    val density = LocalDensity.current

    Row(
        modifier = modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
            .height(height)
    ) {
        Column(
            modifier = Modifier
                .width(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.startTime.substring(11, 16),
                style = MaterialTheme.typography.titleMedium
            )
            item.endTime?.let {
                Text(
                    text = it.substring(11, 16),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }
        }

        Box(
            modifier = Modifier
                .width(24.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            val primaryColor = MaterialTheme.colorScheme.primary
            Canvas(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
            ) {
                val radius = with(density) { 3.dp.toPx() }
                val strokeWidth = with(density) { (1.5).dp.toPx() }
                if (isCurrent) {
                    drawCircle(
                        color = primaryColor,
                        radius = radius * 2,
                        center = Offset(radius, radius * 2),
                        style = Stroke(strokeWidth)
                    )
                    drawCircle(
                        color = primaryColor,
                        radius = radius,
                        center = Offset(radius, radius * 2),
                    )
                } else {
                    drawCircle(
                        color = primaryColor,
                        radius = radius * 1.5f,
                        center = Offset(radius, radius * 1.5f),
                        style = Stroke(strokeWidth)
                    )
                }
                if (isLast.not()) {
                    drawLine(
                        color = primaryColor,
                        start = Offset(radius, radius * if (isCurrent) 6 else 5),
                        end = Offset(radius, size.height),
                        strokeWidth = strokeWidth
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrent) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = item.content,
                        maxLines = contentMaxLines,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "开始时间：${item.startTime}",
                        style = MaterialTheme.typography.labelMedium,
                        color = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                    item.endTime?.let {
                        Text(
                            text = "结束时间：${it}",
                            style = MaterialTheme.typography.labelMedium,
                            color = LocalContentColor.current.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = "是否完成：${CompletionStatusEnum.getByStatus(item.completed)!!.desc}",
                        style = MaterialTheme.typography.labelMedium,
                        color = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                }

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = Icons.Default.Close.name,
                    tint = LocalContentColor.current,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                        .size(16.dp)
                        .noRippleClickable(onClick = onClickDelete)
                )
            }
        }
    }
}
