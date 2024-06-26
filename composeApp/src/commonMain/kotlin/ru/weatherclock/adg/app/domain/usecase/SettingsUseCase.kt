package ru.weatherclock.adg.app.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.data.dto.CityConfig
import ru.weatherclock.adg.app.data.dto.WeatherServer
import ru.weatherclock.adg.app.data.repository.location.LocationRepository
import ru.weatherclock.adg.app.data.repository.location.implementation.AccuweatherLocationRepoImpl
import ru.weatherclock.adg.app.data.repository.location.implementation.OpenWeatherMapLocationRepoImpl
import ru.weatherclock.adg.app.data.repository.settings.*
import ru.weatherclock.adg.app.data.util.isInHours
import ru.weatherclock.adg.app.domain.model.settings.*
import ru.weatherclock.adg.platformSpecific.AutoStartHelper
import ru.weatherclock.adg.platformSpecific.PlatformHelper.ioDispatcher

@OptIn(FlowPreview::class)
class SettingsUseCase(
    private val timeRepo: TimeSettingsRepository,
    private val calendarRepo: CalendarSettingsRepository,
    private val prodCalendarRepo: ProdCalendarSettingsRepository,
    private val weatherRepo: WeatherSettingsRepository,
    private val uiSettingsRepository: UiSettingsRepository,
    private val systemSettingsRepository: SystemSettingsRepository,
    private val accuweatherLocationRepoImpl: AccuweatherLocationRepoImpl,
    private val openWeatherMapLocationRepoImpl: OpenWeatherMapLocationRepoImpl,
) {

    private val autocompleteSearchResults: MutableSharedFlow<List<CityConfig>> = MutableSharedFlow()
    val text = MutableStateFlow("")

    init {
        text.debounce(2000).distinctUntilChanged().mapLatest {
            autocompleteSearch(it)
        }.launchIn(CoroutineScope(ioDispatcher))
    }

    private suspend fun autocompleteSearch(query: String) {
        if (query.length < 3) {
            autocompleteSearchResults.emit(emptyList())
        } else {
            val list = getLocationRepository().autocompleteSearch(
                apiKeys = weatherRepo.getApiKeys(),
                language = weatherRepo.getWeatherLanguage().code,
                query = query
            )
            autocompleteSearchResults.emit(list)
        }
    }

    private suspend fun getLocationRepository(): LocationRepository =
        if (weatherRepo.getWeatherServer() == WeatherServer.Accuweather) {
            accuweatherLocationRepoImpl
        } else {
            openWeatherMapLocationRepoImpl
        }

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
        return uiSettingsRepository.allConfigFlow
    }

    suspend fun getAllSettings(): AppSettings {
        return uiSettingsRepository.getAllConfig()
    }

    fun getSettingsFlow(): Flow<List<BaseSettingItem>> =
        getAllSettingsFlow().mapLatest { settings ->
            val timeConfig = settings.timeConfig
            val weatherSettings = settings.weatherConfig
            val calendarConfig = settings.calendarConfig
            val prodCalendarConfig = calendarConfig.prodCalendarConfig
            val uiConfig = settings.uiConfig
            mutableListOf<BaseSettingItem>().apply {
                if (AutoStartHelper.isSupported) {
                    add(SettingsHeader(settingsKey = SettingKey.HeaderSystem))
                    add(
                        BooleanSetting(settingsKey = SettingKey.AutoStart,
                            currentValue = systemSettingsRepository.getConfig().autoStartEnabled && AutoStartHelper.isEnabled,
                            onChange = {
                                safeUpdate {
                                    systemSettingsRepository.saveConfig {
                                        copy(autoStartEnabled = it)
                                    }
                                }
                                if (it && !AutoStartHelper.isEnabled) {
                                    AutoStartHelper.enable()
                                }
                            })
                    )
                }
                add(SettingsHeader(settingsKey = SettingKey.HeaderTheme))
                add(
                    ColorsThemeSetting(settingsKey = SettingKey.Theme,
                        currentValue = uiConfig.colorTheme,
                        onChange = {
                            safeUpdate {
                                uiSettingsRepository.setColorTheme(it)
                            }
                        })
                )

                val isHideElementsByTimeRange =
                    uiConfig.isTextCalendarHidden || uiConfig.isGridCalendarHidden || uiConfig.isWeatherHidden
                add(
                    BooleanSetting(settingsKey = SettingKey.HideWeatherByTime,
                        currentValue = uiConfig.isWeatherHidden,
                        onChange = {
                            safeUpdate { uiSettingsRepository.setWeatherHiddenByTime(hidden = it) }
                        })
                )
                add(
                    BooleanSetting(settingsKey = SettingKey.HideTextCalendarByTime,
                        currentValue = uiConfig.isTextCalendarHidden,
                        onChange = {
                            safeUpdate { uiSettingsRepository.setTextCalendarHiddenByTime(hidden = it) }
                        })
                )
                add(
                    BooleanSetting(settingsKey = SettingKey.HideGridCalendarByTime,
                        currentValue = uiConfig.isGridCalendarHidden,
                        onChange = {
                            safeUpdate { uiSettingsRepository.setGridCalendarHiddenByTime(hidden = it) }
                        })
                )
                add(
                    HoursRangeSetting(isEnabled = isHideElementsByTimeRange,
                        settingsKey = SettingKey.HideElementsByTimeRange,
                        currentValue = uiConfig.hideStartHour to uiConfig.hideEndHour,
                        onChange = {
                            safeUpdate {
                                uiSettingsRepository.setElementsHideStartHour(it.first)
                                uiSettingsRepository.setElementsHideEndHour(it.second)
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
                        isEnabled = weatherSettings.weatherEnabled,
                        onChange = {
                            safeUpdate {
                                weatherRepo.setWeatherServer(it)
                            }
                        })
                )
                val settingKeyServer = if (weatherSettings.server == WeatherServer.Accuweather) {
                    SettingKey.WeatherApiKeysAccuweather
                } else {
                    SettingKey.WeatherApiKeysOpenweathermap
                }
                add(
                    StringListSetting(
                        settingsKey = settingKeyServer,
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
                add(
                    AutoCompleteSearchSetting(
                        settingsKey = SettingKey.WeatherCityKeySearch,
                        isEnabled = weatherEnabled,
                        onChange = {
                            safeUpdate {
                                weatherRepo.saveConfig {
                                    copy(city = it)
                                }
                            }
                        },
                        currentValue = weatherSettings.city,
                        onSearchTextChanged = {
                            text.value = it
                        },
                        searchResults = autocompleteSearchResults,
                        currentServer = weatherSettings.server
                    )
                )
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