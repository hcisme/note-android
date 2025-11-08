package io.github.hcisme.note.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import io.github.hcisme.note.constants.StorageKeys
import io.github.hcisme.note.enums.ThemeStateEnum
import io.github.hcisme.note.network.model.UserInfoModel

// =======================token
/**
 * 存储token
 */
fun SharedPreferences.getToken(): String? = this.getString(StorageKeys.KEY_TOKEN, null)

/**
 * 获取token
 */
fun SharedPreferences.saveToken(token: String) {
    this.edit {
        putString(StorageKeys.KEY_TOKEN, token).apply()
    }
}

/**
 * 清除token
 */
fun SharedPreferences.clearToken() {
    this.edit {
        remove(StorageKeys.KEY_TOKEN).apply()
    }
}

// ======================用户信息
/**
 * 存储用户信息
 */
fun SharedPreferences.saveUserInfo(userInfo: UserInfoModel) {
    val gson = Gson()
    val userInfoJson = gson.toJson(userInfo)
    this.edit {
        putString(StorageKeys.KEY_USER_INFO, userInfoJson).apply()
    }
}

/**
 * 获取用户信息
 */
fun SharedPreferences.getUserInfo(): UserInfoModel? {
    val userInfoJson = this.getString(StorageKeys.KEY_USER_INFO, null)
    return if (userInfoJson != null) {
        try {
            val gson = Gson()
            gson.fromJson(userInfoJson, UserInfoModel::class.java)
        } catch (_: Exception) {
            null
        }
    } else {
        null
    }
}

/**
 * 清除用户信息
 */
fun SharedPreferences.clearUserInfo() {
    this.edit {
        remove(StorageKeys.KEY_USER_INFO).apply()
    }
}

// ====================== 主题信息
/**
 * 获取主题模式
 */
fun SharedPreferences.getThemeMode(): ThemeStateEnum? {
    val themeMode = this.getInt(StorageKeys.KEY_THEME_MODE, -1)
    return ThemeStateEnum.fromMode(themeMode)
}

/**
 * 保存主题模式
 */
fun SharedPreferences.saveThemeMode(themeMode: Int) {
    this.edit {
        putInt(StorageKeys.KEY_THEME_MODE, themeMode).apply()
    }
}

/**
 * 清除主题模式
 */
fun SharedPreferences.clearThemeMode() {
    this.edit {
        remove(StorageKeys.KEY_THEME_MODE).apply()
    }
}
