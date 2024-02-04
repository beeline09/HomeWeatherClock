package ru.weatherclock.adg.app.data.repository.settings

import kotlinx.coroutines.flow.Flow
import ru.weatherclock.adg.app.data.dto.ProdCalendarConfig

interface ProdCalendarSettingsRepository {

    val config: Flow<ProdCalendarConfig>
    suspend fun getConfig(): ProdCalendarConfig
    suspend fun saveConfig(callback: ProdCalendarConfig.() -> ProdCalendarConfig)

    suspend fun isRussia(): Boolean
    suspend fun setIsRussia(isRussia: Boolean)

    suspend fun getRussianRegionNumber(): Int
    suspend fun setRussianRegionNumber(number: Int)

    suspend fun isDayDescriptionEnabled(): Boolean
    suspend fun setDayDescriptionEnabled(enabled: Boolean)
}