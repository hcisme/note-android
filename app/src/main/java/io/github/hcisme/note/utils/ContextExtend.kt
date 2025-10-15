package io.github.hcisme.note.utils

import android.content.Context
import android.content.SharedPreferences
import io.github.hcisme.note.R

/**
 * 获取[R.string.app_name]的 SharedPreferences
 */
fun Context.getSps(): SharedPreferences {
    return getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
}
