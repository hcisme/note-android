package io.github.hcisme.note.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.delay

data class NotificationState(
    val message: String = "",
    val isVisible: Boolean = false
)

class NotificationManager {
    var notificationState by mutableStateOf(NotificationState())

    fun showNotification(message: String) {
        notificationState = NotificationState(
            message = message,
            isVisible = true
        )
    }

    fun hideNotification() {
        notificationState = notificationState.copy(
            isVisible = false
        )
    }
}

@Composable
fun rememberNotificationManager() = remember { NotificationManager() }

@Composable
fun NotificationPopup(
    notificationState: NotificationState,
    onDismiss: () -> Unit = {}
) {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val windowWidthPx = windowInfo.containerSize.width
    val contentPx = remember { with(density) { 120.dp.toPx() } }
    val yOffsetPx = remember { with(density) { 100.dp.toPx() } }
    val xOffsetPx = remember(windowWidthPx) { (windowWidthPx / 2) - (contentPx / 2) }

    LaunchedEffect(notificationState.isVisible) {
        if (notificationState.isVisible) {
            delay(1400)
            onDismiss()
        }
    }

    Popup(
        offset = IntOffset(x = xOffsetPx.toInt(), y = yOffsetPx.toInt()),
        onDismissRequest = {
            onDismiss()
        }
    ) {
        AnimatedVisibility(
            visible = notificationState.isVisible,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .width(with(density) { contentPx.toDp() })
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = notificationState.message,
                    color = MaterialTheme.colorScheme.background,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
