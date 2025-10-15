package io.github.hcisme.note.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun AnimatedLabelText(
    defaultLabel: String,
    errorMessage: String,
    animationDuration: Int = 300
) {
    val targetText = remember(errorMessage) {
        errorMessage.ifEmpty { defaultLabel }
    }
    val labelColor by animateColorAsState(
        targetValue = if (errorMessage.isNotEmpty()) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(durationMillis = animationDuration),
        label = "labelColor"
    )

    AnimatedContent(
        targetState = targetText,
        transitionSpec = {
            fadeIn(tween(animationDuration / 2)) togetherWith
                    fadeOut(tween(animationDuration / 2))
        }
    ) { currentText ->
        Text(
            text = currentText,
            color = labelColor
        )
    }
}
