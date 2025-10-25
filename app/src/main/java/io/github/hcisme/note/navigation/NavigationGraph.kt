package io.github.hcisme.note.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
    val enterTransition = remember {
        slideInVertically(
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            initialOffsetY = { it / 3 }
        ) + fadeIn(animationSpec = tween(durationMillis = 400))
    }
    val exitTransition = remember {
        slideOutVertically(
            animationSpec = tween(durationMillis = 600, easing = FastOutLinearInEasing),
            targetOffsetY = { it }
        ) + fadeOut(animationSpec = tween(durationMillis = 280))
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
        composable(route = NavigationName.HOME_PAGE) {
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
            enterTransition = { enterTransition },
            popEnterTransition = null,
            popExitTransition = { exitTransition }
        ) { backStackEntry ->
            val idString = backStackEntry.arguments?.getString("id")
            val id = idString?.toLongOrNull()
            TodoFormPage(id = id)
        }

        composable(
            route = NavigationName.SETTING_PAGE,
            enterTransition = { enterTransition },
            popEnterTransition = null,
            popExitTransition = { exitTransition }
        ) {
            SettingPage()
        }
    }
}
