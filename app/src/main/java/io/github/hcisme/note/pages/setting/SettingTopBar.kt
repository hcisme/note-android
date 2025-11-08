package io.github.hcisme.note.pages.setting

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.R
import io.github.hcisme.note.enums.ThemeStateEnum
import io.github.hcisme.note.ui.theme.ThemeViewModel
import io.github.hcisme.note.utils.LocalNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingTopBar(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val themeVM = viewModel<ThemeViewModel>(context as ComponentActivity)

    TopAppBar(
        modifier = modifier,
        title = { Text(text = "设置") },
        navigationIcon = {
            IconButton(
                onClick = { navHostController.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                )
            }
        },
        actions = {
            IconButton(
                onClick = { themeVM.changeTheme() }
            ) {
                val painterId = when (themeVM.currentTheme) {
                    ThemeStateEnum.Light -> R.drawable.light
                    ThemeStateEnum.Dark -> R.drawable.dark
                    ThemeStateEnum.System -> R.drawable.system
                }
                Icon(
                    painter = painterResource(painterId),
                    contentDescription = "主题切换"
                )
            }
        }
    )
}
