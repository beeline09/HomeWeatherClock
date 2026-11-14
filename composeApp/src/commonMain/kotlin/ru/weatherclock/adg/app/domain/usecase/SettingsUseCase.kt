package ru.weatherclock.adg.app.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.data.dto.CityConfig
import ru.weatherclock.adg.app.data.dto.WeatherServer
import ru.weatherclock.adg.app.data.repository.location.LocationRepository
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
    private val accuweatherLocationRepoImpl: LocationRepository,
    private val openWeatherMapLocationRepoImpl: LocationRepository,
) {

    private val _searchState = MutableStateFlow<CitySearchState>(CitySearchState.Idle)
    val searchState: StateFlow<CitySearchState> = _searchState
    val text = MutableStateFlow("")

    /** Текст последнего успешного запроса: тот же текст API повторно не дёргает */
    private var lastQueriedText: String = ""

    init {
        //Дебаунс 1с: спиннер показывается сразу при вводе (см. onSearchTextChanged),
        //реальный запрос уходит с текущим значением текстового поля после паузы
        text.debounce(1000).distinctUntilChanged().mapLatest {
            autocompleteSearch(it)
        }.launchIn(CoroutineScope(ioDispatcher))
    }

    /**
     * Автопоиск населённого пункта. Ошибки запроса (сеть/лимиты/ключ) не
     * должны убивать collector-скоуп — они превращаются в CitySearchState.Error.
     */
    private suspend fun autocompleteSearch(query: String) {
        if (query.length < 3) {
            _searchState.value = CitySearchState.Idle
            return
        }
        // Вернулись к тексту, по которому результат уже показан (стёр и
        // снова набрал то же) — повторный запрос не нужен
        if (query == lastQueriedText && _searchState.value is CitySearchState.Results) {
            return
        }
        _searchState.value = try {
            val list = getLocationRepository().autocompleteSearch(
                apiKeys = weatherRepo.getApiKeys(),
                language = weatherRepo.getWeatherLanguage().code,
                query = query
            )
            lastQueriedText = query
            CitySearchState.Results(list)
        } catch (e: Exception) {
            CitySearchState.Error(e)
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
                add(
                    WeatherIconSetSetting(settingsKey = SettingKey.WeatherIconSet,
                        currentValue = weatherSettings.iconSet,
                        isEnabled = weatherSettings.weatherEnabled,
                        currentServer = weatherSettings.server,
                        onChange = {
                            safeUpdate {
                                weatherRepo.setWeatherIconSet(it)
                            }
                        })
                )
                // У каждого провайдера — СВОЙ массив ключей (раньше список
                // был общий: ключ OWM уезжал в запросы Accuweather)
                add(
                    StringListSetting(
                        settingsKey = SettingKey.WeatherApiKeysAccuweather,
                        isEnabled = weatherSettings.weatherEnabled,
                        currentValue = weatherSettings.apiKeysFor(WeatherServer.Accuweather),
                        onChange = {
                            safeUpdate {
                                weatherRepo.setApiKeys(WeatherServer.Accuweather, it)
                            }
                        })
                )
                add(
                    StringListSetting(
                        settingsKey = SettingKey.WeatherApiKeysOpenweathermap,
                        isEnabled = weatherSettings.weatherEnabled,
                        currentValue = weatherSettings.apiKeysFor(WeatherServer.OpenWeatherMap),
                        onChange = {
                            safeUpdate {
                                weatherRepo.setApiKeys(WeatherServer.OpenWeatherMap, it)
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
                            //Спиннер сразу при вводе (>=3 символов), запрос — после дебаунса
                            _searchState.value = if (it.length >= 3) {
                                CitySearchState.Loading
                            } else {
                                CitySearchState.Idle
                            }
                        },
                        searchResults = searchState,
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