package io.github.hcisme.note

import android.app.Application
import android.content.Context
import android.widget.Toast
import org.acra.config.httpSender
import org.acra.config.limiter
import org.acra.config.toast
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.acra.sender.HttpSender
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        if (!BuildConfig.DEBUG) {
            initialAcra()
        }
    }

    private fun initialAcra() {
        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON

            httpSender {
                uri = BuildConfig.ARCA_URL
                httpMethod = HttpSender.Method.POST
                basicAuthLogin = BuildConfig.ARCA_LOGIN
                basicAuthPassword = BuildConfig.ARCA_PASSWORD
                connectionTimeout = 10000
                socketTimeout = 10000
            }

            toast {
                text = getString(R.string.crash_toast_text)
                length = Toast.LENGTH_LONG
            }

            limiter {
                enabled = true
                periodUnit = TimeUnit.MINUTES
                period = 1
            }
        }
    }
}