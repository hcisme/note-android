package io.github.hcisme.note

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import io.github.hcisme.note.constants.NetworkConstants
import io.github.hcisme.note.navigation.NavigationGraph
import io.github.hcisme.note.network.Request
import io.github.hcisme.note.pages.AuthDialog
import io.github.hcisme.note.pages.AuthViewModel
import io.github.hcisme.note.ui.theme.NoteTheme
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.LocalSharedPreferences

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)

        setContent {
            val navController = rememberNavController()
            val authVM = viewModel<AuthViewModel>()

            LaunchedEffect(Unit) {
                Request.init(baseUrl = NetworkConstants.BASE_URL, authViewModel = authVM)
            }

            CompositionLocalProvider(
                LocalNavController provides navController,
                LocalSharedPreferences provides sharedPreferences
            ) {
                NoteTheme(darkTheme = false, dynamicColor = false) {
                    NavigationGraph()

                    AuthDialog()
                }
            }
        }
    }
}
