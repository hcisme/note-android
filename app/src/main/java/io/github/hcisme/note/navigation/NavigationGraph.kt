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
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.hcisme.note.constants.NavigationName
import io.github.hcisme.note.pages.home.HomePage
import io.github.hcisme.note.pages.login.LoginPage
import io.github.hcisme.note.pages.todoform.TodoFormPage
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.LocalSharedPreferences
import io.github.hcisme.note.utils.getToken

private const val AnimationInDuration = 400
private const val AnimationOutDuration = 200
private val AnimationEasing = LinearOutSlowInEasing
private val enterTransition = slideInHorizontally(
    animationSpec = tween(AnimationInDuration, easing = AnimationEasing),
    initialOffsetX = { it }
)
private val exitTransition = slideOutHorizontally(
    animationSpec = tween(AnimationOutDuration, easing = AnimationEasing),
    targetOffsetX = { it }
)

@Composable
fun NavigationGraph(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    val sharedPreferences = LocalSharedPreferences.current

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
    }
}
