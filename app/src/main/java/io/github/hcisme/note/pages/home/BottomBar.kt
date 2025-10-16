package io.github.hcisme.note.pages.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.hcisme.note.R

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
    currentPage: Int,
    onClick: (key: Int) -> Unit = {}
) {
    Row(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .height(height)
            .background(NavigationBarDefaults.containerColor),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItem(
            pageKey = 0,
            currentPage = currentPage,
            label = "便签",
            iconRes = R.drawable.task,
            onClick = onClick
        )

        BottomBarItem(
            pageKey = 1,
            currentPage = currentPage,
            label = "我的",
            iconRes = R.drawable.user,
            onClick = onClick
        )
    }
}

@Composable
private fun RowScope.BottomBarItem(
    pageKey: Int,
    currentPage: Int,
    label: String,
    iconRes: Int,
    onClick: (key: Int) -> Unit
) {
    val isSelected = currentPage == pageKey
    val color by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = TweenSpec(10),
        label = "bottom_bar_item_color"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable { onClick(pageKey) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = color
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = color
            )
        }
    }
}
