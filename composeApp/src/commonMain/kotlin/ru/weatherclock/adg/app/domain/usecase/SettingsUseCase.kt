package ru.weatherclock.adg.app.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.data.dto.WeatherServer
import ru.weatherclock.adg.app.data.repository.settings.CalendarSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.ProdCalendarSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.TimeSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.UiSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.WeatherSettingsRepository
import ru.weatherclock.adg.app.data.util.isInHours
import ru.weatherclock.adg.app.domain.model.settings.BaseSettingItem
import ru.weatherclock.adg.app.domain.model.settings.BooleanSetting
import ru.weatherclock.adg.app.domain.model.settings.ColorsThemeSetting
import ru.weatherclock.adg.app.domain.model.settings.HoursRangeSetting
import ru.weatherclock.adg.app.domain.model.settings.RussiaRegionSetting
import ru.weatherclock.adg.app.domain.model.settings.SettingKey
import ru.weatherclock.adg.app.domain.model.settings.SettingsHeader
import ru.weatherclock.adg.app.domain.model.settings.StringListSetting
import ru.weatherclock.adg.app.domain.model.settings.StringSetting
import ru.weatherclock.adg.app.domain.model.settings.WeatherApiLanguageSetting
import ru.weatherclock.adg.app.domain.model.settings.WeatherApiListSetting
import ru.weatherclock.adg.platformSpecific.ioDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsUseCase(
    private val timeRepo: TimeSettingsRepository,
    private val calendarRepo: CalendarSettingsRepository,
    private val prodCalendarRepo: ProdCalendarSettingsRepository,
    private val weatherRepo: WeatherSettingsRepository,
    private val mainSettingsRepo: UiSettingsRepository,
) {

    private fun safeUpdate(callback: suspend CoroutineScope.() -> Unit) {
        CoroutineScope(ioDispatcher).launch {
            callback()
        }
    }

    suspend fun isHourlyBeepAllowed(currentHour: Int): Boolean {
        val range = timeRepo.getHourlyBeepStartHour() to timeRepo.getHourlyBeepEndHour()
        return timeRepo.isHourlyBeepEnabled() && currentHour isInHours range
    }

    fun getAllSettingsFlow(): Flow<AppSettings> {
        return mainSettingsRepo.allConfig
    }

    fun getSettingsFlow(): Flow<List<BaseSettingItem>> =
        getAllSettingsFlow().mapLatest { settings ->
            val timeConfig = settings.timeConfig
            val weatherSettings = settings.weatherConfig
            val calendarConfig = settings.calendarConfig
            val prodCalendarConfig = calendarConfig.prodCalendarConfig
            val uiConfig = settings.uiConfig
            mutableListOf<BaseSettingItem>().apply {
                add(SettingsHeader(settingsKey = SettingKey.HeaderTheme))
                add(
                    ColorsThemeSetting(settingsKey = SettingKey.Theme,
                        currentValue = uiConfig.colorTheme,
                        onChange = {
                            safeUpdate {
                                mainSettingsRepo.setColorTheme(it)
                            }
                        })
                )

                val isHideElementsByTimeRange =
                    uiConfig.isTextCalendarHidden || uiConfig.isGridCalendarHidden || uiConfig.isWeatherHidden
                add(
                    BooleanSetting(settingsKey = SettingKey.HideWeatherByTime,
                        currentValue = uiConfig.isWeatherHidden,
                        onChange = {
                            safeUpdate { mainSettingsRepo.setWeatherHiddenByTime(hidden = it) }
                        })
                )
                add(
                    BooleanSetting(settingsKey = SettingKey.HideTextCalendarByTime,
                        currentValue = uiConfig.isTextCalendarHidden,
                        onChange = {
                            safeUpdate { mainSettingsRepo.setTextCalendarHiddenByTime(hidden = it) }
                        })
                )
                add(
                    BooleanSetting(settingsKey = SettingKey.HideGridCalendarByTime,
                        currentValue = uiConfig.isGridCalendarHidden,
                        onChange = {
                            safeUpdate { mainSettingsRepo.setGridCalendarHiddenByTime(hidden = it) }
                        })
                )
                add(
                    HoursRangeSetting(isEnabled = isHideElementsByTimeRange,
                        settingsKey = SettingKey.HideElementsByTimeRange,
                        currentValue = uiConfig.hideStartHour to uiConfig.hideEndHour,
                        onChange = {
                            safeUpdate {
                                mainSettingsRepo.setElementsHideStartHour(it.first)
                                mainSettingsRepo.setElementsHideEndHour(it.second)
                            }
                        })
                )
                add(SettingsHeader(settingsKey = SettingKey.HeaderWeatherConfig))
                add(
                    BooleanSetting(settingsKey = SettingKey.WeatherEnabled,
                        currentValue = weatherSettings.weatherEnabled,
                        onChange = {
                            safeUpdate {
                                weatherRepo.setWeatherEnabled(it)
                            }
                        })
                )
                add(
                    WeatherApiListSetting(settingsKey = SettingKey.WeatherServers,
                        currentValue = weatherSettings.server,
                        onChange = {
                            safeUpdate {
                                weatherRepo.saveConfig {
                                    copy(server = it)
                                }
                            }
                        })
                )
                add(
                    StringListSetting(settingsKey = SettingKey.WeatherApiKeys,
                        isEnabled = weatherSettings.weatherEnabled,
                        currentValue = weatherSettings.weatherApiKeys,
                        onChange = {
                            safeUpdate {
                                weatherRepo.setApiKeys(it)
                            }
                        })
                )
                add(
                    WeatherApiLanguageSetting(settingsKey = SettingKey.WeatherLanguage,
                        currentValue = weatherSettings.weatherApiLanguage,
                        isEnabled = weatherSettings.weatherEnabled,
                        onChange = {
                            safeUpdate {
                                weatherRepo.setWeatherLanguage(it)
                            }
                        })
                )
                val weatherEnabled =
                    weatherSettings.weatherEnabled && weatherSettings.weatherApiKeys.isNotEmpty()
                when (weatherSettings.server) {
                    WeatherServer.Accuweather -> {
                        add(
                            StringSetting(settingsKey = if (weatherEnabled) SettingKey.WeatherCityKey1 else SettingKey.WeatherCityKey2,
                                currentValue = weatherSettings.city.key,
                                isEnabled = weatherEnabled,
                                onChange = { s ->
                                    if (s.isNotBlank()) {
                                        safeUpdate {
                                            weatherRepo.setCityKey(s)
                                        }
                                    }
                                })
                        )
                    }

                    WeatherServer.OpenWeatherMap -> {}
                }
                add(SettingsHeader(settingsKey = SettingKey.HeaderTimeConfig))
                add(
                    BooleanSetting(settingsKey = SettingKey.HoursWithLeadingZero,
                        currentValue = timeConfig.hourWithLeadingZero,
                        onChange = {
                            safeUpdate { timeRepo.setHourWithLeadingZero(withLeadingZero = it) }
                        })
                )
                add(
                    BooleanSetting(
                        settingsKey = SettingKey.DotsFlashEnabled,
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
                    RussiaRegionSetting(settingsKey = SettingKey.ProdCalendarRussiaRegion,
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