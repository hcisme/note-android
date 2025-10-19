package io.github.hcisme.note.utils

import android.content.SharedPreferences
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

val LocalNavController = compositionLocalOf<NavHostController> { error("No NavController found!") }

val LocalSharedPreferences =
    compositionLocalOf<SharedPreferences> { error("No SharedPreferences found!") }
