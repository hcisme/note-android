package io.github.hcisme.note

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import io.github.hcisme.note.components.NotificationPopup
import io.github.hcisme.note.components.rememberNotificationManager
import io.github.hcisme.note.constants.NetworkConstants
import io.github.hcisme.note.navigation.NavigationGraph
import io.github.hcisme.note.network.Request
import io.github.hcisme.note.pages.AuthDialog
import io.github.hcisme.note.pages.rememberAuthManager
import io.github.hcisme.note.ui.theme.NoteTheme
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.LocalNotificationManager
import io.github.hcisme.note.utils.LocalSharedPreferences
import io.github.hcisme.note.utils.getToken

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)

        setContent {
            val navController = rememberNavController()
            val notificationManager = rememberNotificationManager()
            val authManager = rememberAuthManager()

            LaunchedEffect(Unit) {
                Request.init(baseUrl = NetworkConstants.BASE_URL, authManager = authManager) {
                    sharedPreferences.getToken()
                }
            }

            CompositionLocalProvider(
                LocalNavController provides navController,
                LocalSharedPreferences provides sharedPreferences,
                LocalNotificationManager provides notificationManager
            ) {
                NoteTheme(darkTheme = false, dynamicColor = false) {
                    NavigationGraph()

                    AuthDialog(authManager)

                    NotificationPopup(notificationManager.notificationState) {
                        notificationManager.hideNotification()
                    }
                }
            }
        }
    }
}
