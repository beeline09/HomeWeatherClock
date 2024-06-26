package ru.weatherclock.adg

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import ru.weatherclock.adg.app.domain.di.initKoin
import ru.weatherclock.adg.app.domain.usecase.SettingsUseCase


class BootUpReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        runBlocking {
            stopKoin()
            var koin = GlobalContext.getOrNull()
            if (koin == null) {
                koin = initKoin().koin
            }
            if (koin.get<SettingsUseCase>()
                    .getAllSettings().systemConfig.autoStartEnabled
            ) {
                val i = Intent(context, AppActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
            }
        }
    }
}