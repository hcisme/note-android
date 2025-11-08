package io.github.hcisme.note.pages.home.statistics

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.Empty
import io.github.hcisme.note.enums.CompletionStatusEnum
import ir.ehsannarmani.compose_charts.PieChart

@Composable
fun StatisticPieCharts() {
    val context = LocalContext.current
    val statisticsVM = viewModel<StatisticsViewModel>(context as ComponentActivity)

    LaunchedEffect(Unit) {
        statisticsVM.getTodoCompletedCount()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (statisticsVM.pies.sumOf { it.data } <= 0) {
            Empty()
        } else {
            PieChart(
                modifier = Modifier.size(160.dp),
                data = statisticsVM.pies,
                onPieClick = {
                    statisticsVM.onPieClick(it)
                }
            )

            Column {
                statisticsVM.pies.forEach { item ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(
                            modifier = Modifier
                                .size(12.dp)
                                .background(item.color)
                        )
                        Text(
                            text = "${CompletionStatusEnum.getByStatus(item.label!!.toInt())!!.desc} (${item.data.toInt()})",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}
