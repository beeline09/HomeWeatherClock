package ru.weatherclock.adg

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.resourcesFetcher
import org.koin.android.BuildConfig
import ru.weatherclock.adg.app.domain.di.initKoin
import ru.weatherclock.adg.platformSpecific.appStorage
import ru.weatherclock.adg.platformSpecific.defaultHttpClientEngine

class AndroidApp : Application() {
    companion object {

        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        appStorage = filesDir.path

        initKoin(enableNetworkLogs = BuildConfig.DEBUG) {
            //  androidLogger()
            // androidContext(this@App)
        }
    }
}

class AppActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState)
        setContent {
            val kamelConfig = remember {
                KamelConfig {
                    takeFrom(KamelConfig.Default)
                    resourcesFetcher(this@AppActivity)
                    httpFetcher(defaultHttpClientEngine)
                }
            }
            App(
                isDarkThemeSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q,
                kamelConfig = kamelConfig
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController

            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            val decorView = window.decorView
            var uiVisibility = decorView.systemUiVisibility
            uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_LOW_PROFILE
            uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN
            uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE
            uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiVisibility
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(applicationContext)) {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
            }
        }
    }
}

internal actual fun openUrl(url: String?) {
    val uri = url?.let { Uri.parse(it) } ?: return
    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = uri
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    AndroidApp.INSTANCE.startActivity(intent)
}