package ru.weatherclock.adg.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import ru.weatherclock.adg.platformSpecific.dataStoreFileName
import ru.weatherclock.adg.platformSpecific.getDataStore

fun getDataStore1(context: Context): DataStore<Preferences> = getDataStore(
    producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
)