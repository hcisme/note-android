package io.github.hcisme.note.pages.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun YearMonthInput(
    yearValue: String,
    monthValue: String,
    onYearChange: (String) -> Unit,
    onMonthChange: (String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 年份输入框 (4位)
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = yearValue,
                onValueChange = { newValue ->
                    // 只允许数字输入，且最多4位
                    if (newValue.all { it.isDigit() } && newValue.length <= 4) {
                        onYearChange(newValue)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (yearValue.length == 4) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        innerTextField()

                        // 占位符
                        if (yearValue.isEmpty()) {
                            Text(
                                text = "YYYY",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            )

            Text(
                text = "年",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 20.dp)
            )
        }

        Text(
            text = "-",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 月份输入框 (2位)
        Box(
            modifier = Modifier
                .width(70.dp)
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = monthValue,
                onValueChange = { newValue ->
                    // 只允许数字输入，且最多2位
                    if (newValue.all { it.isDigit() } && newValue.length <= 2) {
                        onMonthChange(newValue)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (monthValue.isNotEmpty()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        innerTextField()

                        // 占位符
                        if (monthValue.isEmpty()) {
                            Text(
                                text = "MM",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            )

            Text(
                text = "月",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 20.dp)
            )
        }
    }
}
