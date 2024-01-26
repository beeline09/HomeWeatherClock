package ru.weatherclock.adg.app.domain.model.settings

enum class SettingKey {
    WeatherEnabled,
    WeatherApiKeys,
    WeatherCityKey,
    WeatherLanguage,
    TextCalendarEnabled,
    GridCalendarEnabled,
    ProdCalendarIsRussia,
    ProdCalendarRussiaRegion,
    ProdCalendarDayDescriptionEnabled,
    DotsFlashEnabled,
    DotsFlashAnimated,
    HourlyBeepEnabled,
    HourlyBeepHoursRange,
    HeaderWeatherConfig,
    HeaderTimeConfig,
    HeaderCalendarConfig,
    HeaderProdCalendarConfig,
    HeaderTheme,
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