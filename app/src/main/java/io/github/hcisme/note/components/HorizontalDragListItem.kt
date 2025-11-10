package io.github.hcisme.note.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun HorizontalDragListItem(
    actionWidth: Dp,
    isOpen: Boolean = false,
    onOpenChange: (Boolean) -> Unit = {},
    listHeight: Dp = 72.dp,
    mainContent: @Composable () -> Unit,
    menuContent: @Composable RowScope.() -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatable = remember { Animatable(0f) }
    val onOpenChangeState by rememberUpdatedState(onOpenChange)
    val actionWidthPx = with(density) { actionWidth.toPx() }
    val draggableState = rememberDraggableState { delta ->
        val new = (offsetX + delta).coerceIn(-actionWidthPx, 0f)
        offsetX = new
    }

    fun animateTo(target: Float) {
        scope.launch {
            animatable.snapTo(offsetX)
            animatable.animateTo(target, animationSpec = tween(300)) {
                offsetX = value
            }
        }
    }

    LaunchedEffect(isOpen, actionWidthPx) {
        animateTo(if (isOpen) -actionWidthPx else 0f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(listHeight)
            .background(MaterialTheme.colorScheme.background)
            .clipToBounds()
    ) {
        Row(
            modifier = Modifier
                .width(actionWidth)
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            content = menuContent
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .matchParentSize()
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = draggableState,
                    onDragStopped = {
                        val shouldExpand =
                            offsetX <= if (isOpen) -actionWidthPx / 4f * 3 else -actionWidthPx / 4f
                        if (shouldExpand) {
                            animateTo(-actionWidthPx)
                            onOpenChangeState(true)
                        } else {
                            animateTo(0f)
                            onOpenChangeState(false)
                        }
                    }
                )
        ) {
            mainContent()
            if (isOpen) {
                Spacer(
                    modifier = Modifier
                        .matchParentSize()
                        .pointerInput(Unit) {
                            detectTapGestures {
                                animateTo(0f)
                                onOpenChangeState(false)
                            }
                        }
                )
            }
        }
    }
}
