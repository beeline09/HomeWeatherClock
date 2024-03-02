package ru.weatherclock.adg.app.data.repository.settings.implementation

import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.data.dto.ProdCalendarConfig
import ru.weatherclock.adg.app.data.dto.orDefault
import ru.weatherclock.adg.app.data.repository.settings.ProdCalendarSettingsRepository

class ProdCalendarSettingsRepositoryImpl(private val appSettings: KStore<AppSettings>) :
    ProdCalendarSettingsRepository {

    override val config: Flow<ProdCalendarConfig>
        get() = appSettings.updates.mapLatest { it.orDefault().calendarConfig.prodCalendarConfig }

    override suspend fun getConfig(): ProdCalendarConfig {
        return appSettings.get().orDefault().calendarConfig.prodCalendarConfig
    }

    override suspend fun saveConfig(callback: ProdCalendarConfig.() -> ProdCalendarConfig) {
        appSettings.update {
            val s = it.orDefault()
            s.copy(
                calendarConfig = s.calendarConfig.copy(prodCalendarConfig = callback(s.calendarConfig.prodCalendarConfig))
            )
        }
    }

    override suspend fun isRussia(): Boolean {
        return getConfig().isRussia
    }

    override suspend fun setIsRussia(isRussia: Boolean) {
        saveConfig { copy(isRussia = isRussia) }
    }

    override suspend fun getRussianRegionNumber(): Int {
        return getConfig().russiaRegion
    }

    override suspend fun setRussianRegionNumber(number: Int) {
        saveConfig { copy(russiaRegion = number) }
    }

    override suspend fun isDayDescriptionEnabled(): Boolean {
        return getConfig().dayDescriptionEnabled
    }

    override suspend fun setDayDescriptionEnabled(enabled: Boolean) {
        saveConfig { copy(dayDescriptionEnabled = enabled) }
    }
}