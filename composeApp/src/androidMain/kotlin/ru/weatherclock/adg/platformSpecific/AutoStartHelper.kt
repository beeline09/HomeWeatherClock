package ru.weatherclock.adg.platformSpecific

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.Settings
import ru.weatherclock.adg.AndroidApp
import ru.weatherclock.adg.AppActivity

actual object AutoStartHelper {
    actual val isSupported: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    actual val isEnabled: Boolean
        @SuppressLint("NewApi") get() = isSupported && Settings.canDrawOverlays(AndroidApp.INSTANCE)

    actual fun enable() {
        if (!isSupported) return
        AppActivity.INSTANCE.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
    }

}