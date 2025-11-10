package io.github.hcisme.note.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.hcisme.note.pages.home.HomePage
import io.github.hcisme.note.pages.login.LoginPage
import io.github.hcisme.note.pages.setting.SettingPage
import io.github.hcisme.note.pages.todoform.TodoFormPage
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.LocalSharedPreferences
import io.github.hcisme.note.utils.getToken

@Composable
fun NavigationGraph(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    val sharedPreferences = LocalSharedPreferences.current
    // 新页面从右侧滑入（前进动画）
    val slideInFromRight = remember {
        slideInHorizontally(
            animationSpec = tween(durationMillis = 320, easing = LinearOutSlowInEasing),
            initialOffsetX = { it }
        )
    }
    // 返回时页面从左侧滑入（后退进入动画）
    val slideInFromLeft = remember {
        slideInHorizontally(
            animationSpec = tween(durationMillis = 320, easing = LinearOutSlowInEasing),
            initialOffsetX = { -it }
        )
    }
    // 页面向右侧滑出（前进退出动画）
    val slideOutToRight = remember {
        slideOutHorizontally(
            animationSpec = tween(durationMillis = 320, easing = LinearOutSlowInEasing),
            targetOffsetX = { it }
        )
    }
    // 返回时页面向左侧滑出（后退退出动画）
    val slideOutToLeft = remember {
        slideOutHorizontally(
            animationSpec = tween(durationMillis = 320, easing = LinearOutSlowInEasing),
            targetOffsetX = { -it }
        )
    }

    NavHost(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        navController = navController,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
        startDestination = if (sharedPreferences.getToken() == null) NavigationName.LOGIN_PAGE else NavigationName.HOME_PAGE
    ) {
        composable(
            route = NavigationName.HOME_PAGE,
            popEnterTransition = { slideInFromLeft },
            exitTransition = { slideOutToLeft },
            popExitTransition = null
        ) {
            HomePage()
        }

        composable(route = NavigationName.LOGIN_PAGE) {
            LoginPage()
        }

        composable(
            route = "${NavigationName.TODO_FORM_PAGE}?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            enterTransition = { slideInFromRight },
            popEnterTransition = null,
            popExitTransition = { slideOutToRight }
        ) { backStackEntry ->
            val idString = backStackEntry.arguments?.getString("id")
            val id = idString?.toLongOrNull()
            TodoFormPage(id = id)
        }

        composable(
            route = NavigationName.SETTING_PAGE,
            enterTransition = { slideInFromRight },
            popEnterTransition = null,
            popExitTransition = { slideOutToRight }
        ) {
            SettingPage()
        }
    }
}
