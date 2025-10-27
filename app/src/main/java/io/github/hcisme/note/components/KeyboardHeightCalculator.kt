package io.github.hcisme.note.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun keyboardHeightCalculator(): Dp {
    val density = LocalDensity.current
    val insets = WindowInsets.ime
    val keyboardHeightPx = insets.getBottom(density)
    return with(density) { keyboardHeightPx.toDp() }
}
