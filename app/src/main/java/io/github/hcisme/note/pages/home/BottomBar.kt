package io.github.hcisme.note.pages.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.hcisme.note.enums.BottomBarEnum
import io.github.hcisme.note.utils.noRippleClickable

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
    currentBottomBarEnum: BottomBarEnum,
    onClick: (bottomBarEnum: BottomBarEnum) -> Unit = {}
) {
    Row(
        modifier = modifier
            .background(NavigationBarDefaults.containerColor)
            .navigationBarsPadding()
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItem(
            currentPage = currentBottomBarEnum.index,
            bottomBarEnum = BottomBarEnum.Note,
            onClick = onClick
        )

        BottomBarItem(
            currentPage = currentBottomBarEnum.index,
            bottomBarEnum = BottomBarEnum.Statistic,
            onClick = onClick
        )

        BottomBarItem(
            currentPage = currentBottomBarEnum.index,
            bottomBarEnum = BottomBarEnum.User,
            onClick = onClick
        )
    }
}

@Composable
private fun RowScope.BottomBarItem(
    currentPage: Int,
    bottomBarEnum: BottomBarEnum,
    onClick: (bottomBarEnum: BottomBarEnum) -> Unit
) {
    val isSelected = remember(currentPage, bottomBarEnum) { currentPage == bottomBarEnum.index }
    val color by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = TweenSpec(10),
        label = "${bottomBarEnum.label}_bottom_bar_item_color"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .noRippleClickable { onClick(bottomBarEnum) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(bottomBarEnum.resourceId),
                contentDescription = bottomBarEnum.label,
                tint = color
            )
            Text(
                text = bottomBarEnum.label,
                fontSize = 12.sp,
                color = color
            )
        }
    }
}
