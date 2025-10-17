package io.github.hcisme.note.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class AuthManager {
    var loginDialogVisible by mutableStateOf(false)

    fun showLoginDialog() {
        loginDialogVisible = true
    }

    fun hideLoginDialog() {
        loginDialogVisible = false
    }
}

@Composable
fun rememberAuthManager() = remember { AuthManager() }
