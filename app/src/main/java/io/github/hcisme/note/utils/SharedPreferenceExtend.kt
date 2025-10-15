package io.github.hcisme.note.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import io.github.hcisme.note.constants.StorageKeys

fun SharedPreferences.getToken(): String? = this.getString(StorageKeys.KEY_TOKEN, null)

fun SharedPreferences.saveToken(token: String) {
    this.edit {
        putString(StorageKeys.KEY_TOKEN, token).apply()
    }
}

fun SharedPreferences.clearToken() {
    this.edit {
        remove(StorageKeys.KEY_TOKEN).apply()
    }
}
