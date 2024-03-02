package ru.weatherclock.adg.app.domain.model.settings

enum class SettingKey {
    HideElementsByTime,
    HideElementsByTimeRange,
    HideWeatherByTime,
    HideTextCalendarByTime,
    HideGridCalendarByTime,
    WeatherEnabled,
    WeatherServers,
    WeatherApiKeys,
    WeatherCityKey1,
    WeatherCityKey2,
    WeatherLanguage,
    TextCalendarEnabled,
    GridCalendarEnabled,
    ProdCalendarIsRussia,
    ProdCalendarRussiaRegion,
    ProdCalendarDayDescriptionEnabled,
    HoursWithLeadingZero,
    DotsFlashEnabled,
    DotsFlashAnimated,
    HourlyBeepEnabled,
    HourlyBeepHoursRange,
    HeaderWeatherConfig,
    HeaderTimeConfig,
    HeaderCalendarConfig,
    HeaderProdCalendarConfig,
    HeaderTheme,
    HeaderSystem,
    AutoStart,
    Theme;

    companion object {

        val headers = listOf(
            HeaderTheme,
            HeaderTimeConfig,
            HeaderWeatherConfig,
            HeaderCalendarConfig,
            HeaderProdCalendarConfig,
        )
    }
}