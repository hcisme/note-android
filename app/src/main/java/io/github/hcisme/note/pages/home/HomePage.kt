package io.github.hcisme.note.pages.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.pages.home.task.TaskPage
import io.github.hcisme.note.pages.home.user.UserPage

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val holder = rememberSaveableStateHolder()
    val homeVM = viewModel<HomeViewModel>()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            holder.SaveableStateProvider(homeVM.currentPage) {
                when (homeVM.currentPage) {
                    0 -> TaskPage()
                    1 -> UserPage()
                }
            }
        }
        BottomBar(currentPage = homeVM.currentPage) { homeVM.changePage(page = it) }
    }

    BackHandler {
        (context as Activity).finish()
    }
}
