package io.github.hcisme.note.utils

import android.content.SharedPreferences
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import io.github.hcisme.note.components.NotificationManager

val LocalNavController = compositionLocalOf<NavHostController> { error("No NavController found!") }

val LocalSharedPreferences = compositionLocalOf<SharedPreferences> { error("No SharedPreferences found!") }

val LocalNotificationManager = compositionLocalOf<NotificationManager> { error("No NotificationManager found!") }
