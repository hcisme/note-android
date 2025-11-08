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
import io.github.hcisme.note.components.NotificationManager
import io.github.hcisme.note.components.NotificationPopup
import io.github.hcisme.note.navigation.NavigationGraph
import io.github.hcisme.note.network.Request
import io.github.hcisme.note.pages.AuthDialog
import io.github.hcisme.note.pages.rememberAuthManager
import io.github.hcisme.note.ui.theme.NoteTheme
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.LocalSharedPreferences
import io.github.hcisme.note.utils.getSps
import io.github.hcisme.note.utils.getToken

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val sharedPreferences = this.getSps()

        setContent {
            val navController = rememberNavController()
            val authManager = rememberAuthManager()

            LaunchedEffect(Unit) {
                Request.init(baseUrl = BuildConfig.BASE_URL, authManager = authManager) {
                    sharedPreferences.getToken()
                }
            }

            CompositionLocalProvider(
                LocalNavController provides navController,
                LocalSharedPreferences provides sharedPreferences
            ) {
                NoteTheme(dynamicColor = false) {
                    NavigationGraph()

                    AuthDialog(authManager)

                    NotificationPopup(NotificationManager.notificationState) {
                        NotificationManager.hideNotification()
                    }
                }
            }
        }
    }
}
