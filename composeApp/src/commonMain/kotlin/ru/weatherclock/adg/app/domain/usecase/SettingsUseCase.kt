package ru.weatherclock.adg.app.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import io.github.xxfast.kstore.KStore
import ru.weatherclock.adg.app.domain.model.settings.AppSettings
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
import ru.weatherclock.adg.platformSpecific.safeUpdate

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsUseCase(private val appSettings: KStore<AppSettings>) {

    fun getSettingsFlow(): Flow<List<BaseSettingItem>> = appSettings.updates.mapLatest {
        val settings = it.orDefault()
        val timeConfig = settings.timeConfig
        val weatherConfig = settings.weatherConfig
        val calendarConfig = settings.calendarConfig
        val prodCalendarConfig = calendarConfig.prodCalendarConfig
        mutableListOf<BaseSettingItem>().apply {
            add(SettingsHeader(settingsKey = SettingKey.HeaderTheme))
            add(
                ColorsThemeSetting(
                    settingsKey = SettingKey.Theme,
                    currentValue = settings.colorTheme,
                    onChange = {
                        appSettings.safeUpdate {
                            copy(colorTheme = it)
                        }
                    }
                )
            )
            add(SettingsHeader(settingsKey = SettingKey.HeaderWeatherConfig))
            add(
                BooleanSetting(settingsKey = SettingKey.WeatherEnabled,
                    currentValue = weatherConfig.weatherEnabled,
                    onChange = {
                        appSettings.safeUpdate {
                            copy(
                                weatherConfig = weatherConfig.copy(
                                    weatherEnabled = it
                                )
                            )
                        }
                    })
            )
            add(
                StringListSetting(settingsKey = SettingKey.WeatherApiKeys,
                    isEnabled = weatherConfig.weatherEnabled,
                    currentValue = weatherConfig.weatherApiKeys,
                    onChange = {
                        appSettings.safeUpdate {
                            copy(
                                weatherConfig = weatherConfig.copy(weatherApiKeys = it)
                            )
                        }
                    })
            )
            add(
                StringSetting(settingsKey = SettingKey.WeatherCityKey,
                    currentValue = weatherConfig.weatherCityKey,
                    isEnabled = weatherConfig.weatherEnabled,
                    onChange = { s ->
                        if (s.isNotBlank()) {
                            appSettings.safeUpdate {
                                copy(
                                    weatherConfig = weatherConfig.copy(weatherCityKey = s)
                                )
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
                            appSettings.safeUpdate {
                                copy(
                                    weatherConfig = weatherConfig.copy(weatherLanguage = s)
                                )
                            }
                        }
                    })
            )
            add(SettingsHeader(settingsKey = SettingKey.HeaderTimeConfig))
            add(
                BooleanSetting(settingsKey = SettingKey.DotsFlashEnabled,
                    currentValue = timeConfig.dotsFlashEnabled,
                    onChange = {
                        appSettings.safeUpdate {
                            copy(
                                timeConfig = timeConfig.copy(dotsFlashEnabled = it)
                            )
                        }
                    })
            )
            add(
                BooleanSetting(settingsKey = SettingKey.DotsFlashAnimated,
                    isEnabled = timeConfig.dotsFlashEnabled,
                    currentValue = timeConfig.dotsFlashAnimated,
                    onChange = {
                        appSettings.safeUpdate {
                            copy(
                                timeConfig = timeConfig.copy(dotsFlashAnimated = it)
                            )
                        }
                    })
            )
            add(
                BooleanSetting(settingsKey = SettingKey.HourlyBeepEnabled,
                    currentValue = timeConfig.hourlyBeepEnabled,
                    onChange = {
                        appSettings.safeUpdate {
                            copy(
                                timeConfig = timeConfig.copy(hourlyBeepEnabled = it)
                            )
                        }
                    })
            )
            add(
                HoursRangeSetting(isEnabled = timeConfig.hourlyBeepEnabled,
                    settingsKey = SettingKey.HourlyBeepHoursRange,
                    currentValue = timeConfig.hourlyBeepStartHour to timeConfig.hourlyBeepEndHour,
                    onChange = {
                        appSettings.safeUpdate {
                            copy(
                                timeConfig = timeConfig.copy(
                                    hourlyBeepStartHour = it.first,
                                    hourlyBeepEndHour = it.second
                                )
                            )
                        }
                    })
            )
            add(SettingsHeader(settingsKey = SettingKey.HeaderCalendarConfig))
            add(
                BooleanSetting(settingsKey = SettingKey.TextCalendarEnabled,
                    currentValue = calendarConfig.textCalendarEnabled,
                    onChange = {
                        appSettings.safeUpdate {
                            copy(calendarConfig = calendarConfig.copy(textCalendarEnabled = it))
                        }
                    })
            )
            add(
                BooleanSetting(settingsKey = SettingKey.GridCalendarEnabled,
                    currentValue = calendarConfig.gridCalendarEnabled,
                    onChange = {
                        appSettings.safeUpdate {
                            copy(calendarConfig = calendarConfig.copy(gridCalendarEnabled = it))
                        }
                    })
            )
            add(SettingsHeader(settingsKey = SettingKey.HeaderProdCalendarConfig))
            add(
                BooleanSetting(settingsKey = SettingKey.ProdCalendarIsRussia,
                    currentValue = prodCalendarConfig.isRussia,
                    onChange = {
                        appSettings.safeUpdate {
                            copy(calendarConfig = calendarConfig.copy(prodCalendarConfig = prodCalendarConfig.copy(isRussia = it)))
                        }
                    })
            )
            add(
                IntSetting(settingsKey = SettingKey.ProdCalendarRussiaRegion,
                    isEnabled = prodCalendarConfig.isRussia,
                    currentValue = prodCalendarConfig.russiaRegion,
                    onChange = {
                        appSettings.safeUpdate {
                            copy(calendarConfig = calendarConfig.copy(prodCalendarConfig = prodCalendarConfig.copy(russiaRegion = it)))
                        }
                    })
            )
            add(
                BooleanSetting(settingsKey = SettingKey.ProdCalendarDayDescriptionEnabled,
                    currentValue = prodCalendarConfig.isRussia,
                    onChange = {
                        appSettings.safeUpdate {
                            copy(calendarConfig = calendarConfig.copy(prodCalendarConfig = prodCalendarConfig.copy(dayDescriptionEnabled = it)))
                        }
                    })
            )
        }
    }
}