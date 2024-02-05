package ru.weatherclock.adg.app.presentation.screens.settings.components.utils

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import ru.weatherclock.adg.MR
import ru.weatherclock.adg.app.domain.model.settings.SettingKey

@Composable
fun SettingKey.getName(): String = when (this) {
    SettingKey.WeatherEnabled -> stringResource(MR.strings.setting_title_weather_enabled)
    SettingKey.WeatherApiKeys -> stringResource(MR.strings.setting_title_weather_api_keys)
    SettingKey.WeatherCityKey1, SettingKey.WeatherCityKey2 -> stringResource(MR.strings.setting_title_weather_city_key)
    SettingKey.WeatherLanguage -> stringResource(MR.strings.setting_title_weather_language)
    SettingKey.TextCalendarEnabled -> stringResource(MR.strings.setting_title_text_calendar_enabled)
    SettingKey.GridCalendarEnabled -> stringResource(MR.strings.setting_title_grid_calendar_enabled)
    SettingKey.ProdCalendarIsRussia -> stringResource(MR.strings.setting_title_prod_calendar_is_russia)
    SettingKey.ProdCalendarRussiaRegion -> stringResource(MR.strings.setting_title_prod_calendar_russia_region)
    SettingKey.ProdCalendarDayDescriptionEnabled -> stringResource(MR.strings.setting_title_prod_calendar_day_description_enabled)
    SettingKey.DotsFlashEnabled -> stringResource(MR.strings.setting_title_dots_flash_enabled)
    SettingKey.DotsFlashAnimated -> stringResource(MR.strings.setting_title_dots_flash_animated)
    SettingKey.HourlyBeepEnabled -> stringResource(MR.strings.setting_title_hourly_beep_enabled)
    SettingKey.HourlyBeepHoursRange -> stringResource(MR.strings.setting_title_hourly_beep_hours_range)
    SettingKey.HeaderWeatherConfig -> stringResource(MR.strings.setting_title_header_weather_config)
    SettingKey.HeaderTimeConfig -> stringResource(MR.strings.setting_title_header_time_config)
    SettingKey.HeaderCalendarConfig -> stringResource(MR.strings.setting_title_header_calendar_config)
    SettingKey.HeaderProdCalendarConfig -> stringResource(MR.strings.setting_title_header_prod_calendar_config)
    SettingKey.HeaderTheme -> stringResource(MR.strings.setting_title_header_theme)
    SettingKey.Theme -> stringResource(MR.strings.setting_title_theme)
    SettingKey.HideElementsByTime -> stringResource(MR.strings.setting_title_hide_elements_by_time)
    SettingKey.HideElementsByTimeRange -> stringResource(MR.strings.setting_title_hide_elements_by_hours_range)
    SettingKey.HideWeatherByTime -> stringResource(MR.strings.setting_title_hide_weather_by_time)
    SettingKey.HideTextCalendarByTime -> stringResource(MR.strings.setting_title_hide_text_calendar_by_time)
    SettingKey.HideGridCalendarByTime -> stringResource(MR.strings.setting_title_hide_grid_calendar_by_time)
    SettingKey.HoursWithLeadingZero -> stringResource(MR.strings.setting_title_hours_with_leading_zero)
    SettingKey.WeatherServers -> stringResource(MR.strings.setting_title_weather_server)
}