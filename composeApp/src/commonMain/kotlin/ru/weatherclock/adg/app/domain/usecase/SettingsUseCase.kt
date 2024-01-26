package ru.weatherclock.adg.app.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import ru.weatherclock.adg.app.data.repository.settings.CalendarSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.MainSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.ProdCalendarSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.TimeSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.WeatherSettingsRepository
import ru.weatherclock.adg.app.domain.model.settings.BaseSettingItem
import ru.weatherclock.adg.app.domain.model.settings.BooleanSetting
import ru.weatherclock.adg.app.domain.model.settings.ColorsThemeSetting
import ru.weatherclock.adg.app.domain.model.settings.HoursRangeSetting
import ru.weatherclock.adg.app.domain.model.settings.IntSetting
import ru.weatherclock.adg.app.domain.model.settings.SettingKey
import ru.weatherclock.adg.app.domain.model.settings.SettingsHeader
import ru.weatherclock.adg.app.domain.model.settings.StringListSetting
import ru.weatherclock.adg.app.domain.model.settings.StringSetting
import ru.weatherclock.adg.app.domain.model.settings.orDefault
import ru.weatherclock.adg.platformSpecific.ioDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsUseCase(
    private val timeRepo: TimeSettingsRepository,
    private val calendarRepo: CalendarSettingsRepository,
    private val prodCalendarRepo: ProdCalendarSettingsRepository,
    private val weatherRepo: WeatherSettingsRepository,
    private val mainSettingsRepo: MainSettingsRepository,
) {

    private fun safeUpdate(callback: suspend CoroutineScope.() -> Unit) {
        CoroutineScope(ioDispatcher).launch {
            this.callback()
        }
    }

    fun getSettingsFlow(): Flow<List<BaseSettingItem>> = mainSettingsRepo.config.mapLatest {
        val settings = it.orDefault()
        val timeConfig = settings.timeConfig
        val weatherConfig = settings.weatherConfig
        val calendarConfig = settings.calendarConfig
        val prodCalendarConfig = calendarConfig.prodCalendarConfig
        mutableListOf<BaseSettingItem>().apply {
            add(SettingsHeader(settingsKey = SettingKey.HeaderTheme))
            add(
                ColorsThemeSetting(settingsKey = SettingKey.Theme,
                    currentValue = settings.colorTheme,
                    onChange = {
                        safeUpdate {
                            mainSettingsRepo.setColorTheme(it)
                        }
                    })
            )
            add(SettingsHeader(settingsKey = SettingKey.HeaderWeatherConfig))
            add(
                BooleanSetting(settingsKey = SettingKey.WeatherEnabled,
                    currentValue = weatherConfig.weatherEnabled,
                    onChange = {
                        safeUpdate {
                            weatherRepo.setWeatherEnabled(it)
                        }
                    })
            )
            add(
                StringListSetting(settingsKey = SettingKey.WeatherApiKeys,
                    isEnabled = weatherConfig.weatherEnabled,
                    currentValue = weatherConfig.weatherApiKeys,
                    onChange = {
                        safeUpdate {
                            weatherRepo.setApiKeys(it)
                        }
                    })
            )
            add(
                StringSetting(settingsKey = SettingKey.WeatherCityKey,
                    currentValue = weatherConfig.weatherCityKey,
                    isEnabled = weatherConfig.weatherEnabled,
                    onChange = { s ->
                        if (s.isNotBlank()) {
                            safeUpdate {
                                weatherRepo.setCityKey(s)
                            }
                        }
                    })
            )
            add(
                StringSetting(settingsKey = SettingKey.WeatherLanguage,
                    currentValue = weatherConfig.weatherLanguage,
                    isEnabled = weatherConfig.weatherEnabled,
                    onChange = { s ->
                        if (s.isNotBlank()) {
                            safeUpdate {
                                weatherRepo.setWeatherLanguage(s)
                            }
                        }
                    })
            )
            add(SettingsHeader(settingsKey = SettingKey.HeaderTimeConfig))
            add(
                BooleanSetting(settingsKey = SettingKey.DotsFlashEnabled,
                    currentValue = timeConfig.dotsFlashEnabled,
                    onChange = {
                        safeUpdate {
                            timeRepo.setDotsFlashEnabled(it)
                        }
                    })
            )
            add(
                BooleanSetting(settingsKey = SettingKey.DotsFlashAnimated,
                    isEnabled = timeConfig.dotsFlashEnabled,
                    currentValue = timeConfig.dotsFlashAnimated,
                    onChange = {
                        safeUpdate {
                            timeRepo.setDotsAnimated(it)
                        }
                    })
            )
            add(
                BooleanSetting(settingsKey = SettingKey.HourlyBeepEnabled,
                    currentValue = timeConfig.hourlyBeepEnabled,
                    onChange = {
                        safeUpdate {
                            timeRepo.setHourlyBeepEnabled(it)
                        }
                    })
            )
            add(
                HoursRangeSetting(isEnabled = timeConfig.hourlyBeepEnabled,
                    settingsKey = SettingKey.HourlyBeepHoursRange,
                    currentValue = timeConfig.hourlyBeepStartHour to timeConfig.hourlyBeepEndHour,
                    onChange = {
                        safeUpdate {
                            timeRepo.setHourlyBeepStartHour(it.first)
                            timeRepo.setHourlyBeepEndHour(it.second)
                        }
                    })
            )
            add(SettingsHeader(settingsKey = SettingKey.HeaderCalendarConfig))
            add(
                BooleanSetting(settingsKey = SettingKey.TextCalendarEnabled,
                    currentValue = calendarConfig.textCalendarEnabled,
                    onChange = {
                        safeUpdate {
                            calendarRepo.setTextCalendarEnabled(it)
                        }
                    })
            )
            add(
                BooleanSetting(settingsKey = SettingKey.GridCalendarEnabled,
                    currentValue = calendarConfig.gridCalendarEnabled,
                    onChange = {
                        safeUpdate {
                            calendarRepo.setGridCalendarEnabled(it)
                        }
                    })
            )
            add(SettingsHeader(settingsKey = SettingKey.HeaderProdCalendarConfig))
            add(
                BooleanSetting(settingsKey = SettingKey.ProdCalendarIsRussia,
                    currentValue = prodCalendarConfig.isRussia,
                    onChange = {
                        safeUpdate {
                            prodCalendarRepo.setIsRussia(it)
                        }
                    })
            )
            add(
                IntSetting(settingsKey = SettingKey.ProdCalendarRussiaRegion,
                    isEnabled = prodCalendarConfig.isRussia,
                    currentValue = prodCalendarConfig.russiaRegion,
                    onChange = {
                        safeUpdate {
                            prodCalendarRepo.setRussianRegionNumber(it)
                        }
                    })
            )
            add(
                BooleanSetting(settingsKey = SettingKey.ProdCalendarDayDescriptionEnabled,
                    currentValue = prodCalendarConfig.dayDescriptionEnabled,
                    isEnabled = prodCalendarConfig.isRussia,
                    onChange = {
                        safeUpdate {
                            prodCalendarRepo.setDayDescriptionEnabled(it)
                        }
                    })
            )
        }
    }
}