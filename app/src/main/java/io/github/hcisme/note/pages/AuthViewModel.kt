package io.github.hcisme.note.pages

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    var loginDialogVisible by mutableStateOf(false)

    fun showLoginDialog() {
        loginDialogVisible = true
    }

    fun hideLoginDialog() {
        loginDialogVisible = false
    }
}
