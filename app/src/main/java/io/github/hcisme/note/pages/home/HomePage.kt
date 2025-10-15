package io.github.hcisme.note.pages.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.github.hcisme.note.utils.LocalSharedPreferences
import io.github.hcisme.note.utils.getUserInfo

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPreferences = LocalSharedPreferences.current

    Scaffold(
        topBar = { TimelineTopBar() },
        bottomBar = {}
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(sharedPreferences.getUserInfo()?.username ?: "暂无信息")
        }
    }

    BackHandler {
        (context as Activity).finish()
    }
}