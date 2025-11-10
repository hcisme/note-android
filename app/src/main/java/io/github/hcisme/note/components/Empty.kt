package io.github.hcisme.note.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.hcisme.note.R

@Composable
fun Empty(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(64.dp),
                painter = painterResource(R.drawable.nothing),
                contentDescription = null
            )
            Text(
                text = "暂无数据",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
