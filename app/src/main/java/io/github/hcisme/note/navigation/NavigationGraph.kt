package io.github.hcisme.note.navigation

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.hcisme.note.constants.NavigationName
import io.github.hcisme.note.pages.home.HomePage
import io.github.hcisme.note.pages.login.LoginPage
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.LocalSharedPreferences
import io.github.hcisme.note.utils.getToken

private const val AnimationInDuration = 300
private const val AnimationOutDuration = 300
private val AnimationEasing = LinearOutSlowInEasing
private val enterTransition = slideInHorizontally(
    animationSpec = tween(AnimationInDuration, easing = AnimationEasing), initialOffsetX = { it })
private val exitTransition = slideOutHorizontally(
    animationSpec = tween(AnimationOutDuration, easing = AnimationEasing), targetOffsetX = { it })

@Composable
fun NavigationGraph(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    val sharedPreferences = LocalSharedPreferences.current

    NavHost(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = if (sharedPreferences.getToken() == null) NavigationName.LOGIN_PAGE else NavigationName.HOME_PAGE,
//        startDestination = NavigationName.HOME_PAGE,
    ) {
        composable(route = NavigationName.HOME_PAGE) {
            HomePage()
        }

        composable(route = NavigationName.LOGIN_PAGE) {
            LoginPage()
        }
    }
}