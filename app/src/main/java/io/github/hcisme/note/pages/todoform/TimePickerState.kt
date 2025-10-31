package io.github.hcisme.note.pages.todoform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.unit.IntOffset
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.roundToInt

@Stable
class TimePickerState {
    var visible by mutableStateOf(false)
    var anchorOffset by mutableStateOf<IntOffset?>(null)
    val coordsRef = AtomicReference<LayoutCoordinates?>(null)

    fun open() {
        val coords = coordsRef.get()
        coords?.let {
            anchorOffset = IntOffset(
                x = it.boundsInWindow().left.roundToInt(),
                y = it.boundsInWindow().bottom.roundToInt()
            )
            visible = true
        }
    }

    fun close() {
        visible = false
    }
}

@Composable
fun rememberTimePickerState() = remember { TimePickerState() }
