package io.github.hcisme.note.utils

import android.graphics.Paint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

fun Modifier.withBadge(
    showBadge: Boolean = true,
    badgeColor: Color = Color.Red,
    badgeSize: ContentDrawScope.() -> Float = { 3.dp.toPx() },
    badgeText: String? = null,
    textColor: Color = badgeColor,
    textSize: ContentDrawScope.() -> Float = { 16.dp.toPx() },
    offset: ContentDrawScope.() -> Offset = { Offset(size.width, 0f) }
): Modifier = this.then(
    if (showBadge) {
        Modifier.drawWithContent {
            drawContent()

            if (badgeText != null) {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        this@apply.color = textColor.toArgb()
                        this@apply.textSize = textSize()
                        this@apply.textAlign = Paint.Align.CENTER
                        this@apply.isAntiAlias = true
                    }

                    val center = offset()
                    // 计算文字基线，使其垂直居中
                    val fontMetrics = paint.fontMetrics
                    val baseline = (fontMetrics.descent + fontMetrics.ascent) / 2

                    canvas.nativeCanvas.drawText(
                        badgeText,
                        center.x,
                        center.y - baseline,
                        paint
                    )
                }
            } else {
                drawCircle(
                    color = badgeColor,
                    radius = badgeSize(),
                    center = offset()
                )
            }
        }
    } else {
        Modifier
    }
)

fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    this.clickable(
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}
