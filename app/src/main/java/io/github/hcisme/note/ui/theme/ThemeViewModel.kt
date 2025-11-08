package io.github.hcisme.note.ui.theme

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.hcisme.note.enums.ThemeStateEnum
import io.github.hcisme.note.utils.getSps
import io.github.hcisme.note.utils.getThemeMode
import io.github.hcisme.note.utils.saveThemeMode
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSps()
    var currentTheme by mutableStateOf(sharedPreferences.getThemeMode() ?: ThemeStateEnum.Light)

    fun changeTheme(themeState: ThemeStateEnum = currentTheme.next()) {
        viewModelScope.launch {
            currentTheme = themeState
            sharedPreferences.saveThemeMode(currentTheme.themeMode)
        }
    }
}