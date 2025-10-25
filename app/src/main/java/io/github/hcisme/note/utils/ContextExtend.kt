package io.github.hcisme.note.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import io.github.hcisme.note.R

/**
 * 获取[R.string.app_name]的 SharedPreferences
 */
fun Context.getSps(): SharedPreferences {
    return getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
}

/**
 * 去设置中的应用界面
 */
fun Context.startSettingActivity(tooltip: String) {
    this.startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", this@startSettingActivity.packageName, null)
        }
    )
    Toast.makeText(this, tooltip, Toast.LENGTH_LONG).show()
}
