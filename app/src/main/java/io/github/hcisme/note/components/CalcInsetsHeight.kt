package io.github.hcisme.note.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

@Composable
fun calcStatusBarHeight(): Int {
    val density = LocalDensity.current
    return WindowInsets.statusBars.getTop(density)
}

@Composable
fun calcNavigationBarHeight(): Int {
    val density = LocalDensity.current
    return WindowInsets.statusBars.getBottom(density)
}
