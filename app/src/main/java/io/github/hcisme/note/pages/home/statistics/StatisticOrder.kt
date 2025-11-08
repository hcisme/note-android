package io.github.hcisme.note.pages.home.statistics

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.utils.noRippleClickable

@Composable
fun StatisticOrder() {
    val context = LocalContext.current
    val statisticsVM = viewModel<StatisticsViewModel>(context as ComponentActivity)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = statisticsVM.sortEnum.message,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.noRippleClickable { statisticsVM.changeOrder() }
        )
        IconButton(
            onClick = {
                statisticsVM.changeOrder()
            }
        ) {
            Icon(
                painter = painterResource(statisticsVM.sortEnum.resourceId),
                contentDescription = statisticsVM.sortEnum.message,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
