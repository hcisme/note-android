package io.github.hcisme.note.pages.setting

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ClearCacheItem(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val settingVM = viewModel<SettingViewModel>(context as ComponentActivity)

    ListItem(
        modifier = modifier.clickable { settingVM.clearCache() },
        headlineContent = { Text(text = "清除缓存") },
        tonalElevation = 1.dp,
        shadowElevation = 4.dp
    )
}
