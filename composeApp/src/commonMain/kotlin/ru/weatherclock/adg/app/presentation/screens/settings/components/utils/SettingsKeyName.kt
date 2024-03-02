package ru.weatherclock.adg.app.presentation.screens.settings.components.utils

import androidx.compose.runtime.Composable
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.setting_description_system
import homeweatherclock.composeapp.generated.resources.setting_title_dots_flash_animated
import homeweatherclock.composeapp.generated.resources.setting_title_dots_flash_enabled
import homeweatherclock.composeapp.generated.resources.setting_title_grid_calendar_enabled
import homeweatherclock.composeapp.generated.resources.setting_title_header_calendar_config
import homeweatherclock.composeapp.generated.resources.setting_title_header_prod_calendar_config
import homeweatherclock.composeapp.generated.resources.setting_title_header_theme
import homeweatherclock.composeapp.generated.resources.setting_title_header_time_config
import homeweatherclock.composeapp.generated.resources.setting_title_header_weather_config
import homeweatherclock.composeapp.generated.resources.setting_title_hide_elements_by_hours_range
import homeweatherclock.composeapp.generated.resources.setting_title_hide_elements_by_time
import homeweatherclock.composeapp.generated.resources.setting_title_hide_grid_calendar_by_time
import homeweatherclock.composeapp.generated.resources.setting_title_hide_text_calendar_by_time
import homeweatherclock.composeapp.generated.resources.setting_title_hide_weather_by_time
import homeweatherclock.composeapp.generated.resources.setting_title_hourly_beep_enabled
import homeweatherclock.composeapp.generated.resources.setting_title_hourly_beep_hours_range
import homeweatherclock.composeapp.generated.resources.setting_title_hours_with_leading_zero
import homeweatherclock.composeapp.generated.resources.setting_title_prod_calendar_day_description_enabled
import homeweatherclock.composeapp.generated.resources.setting_title_prod_calendar_is_russia
import homeweatherclock.composeapp.generated.resources.setting_title_prod_calendar_russia_region
import homeweatherclock.composeapp.generated.resources.setting_title_system
import homeweatherclock.composeapp.generated.resources.setting_title_text_calendar_enabled
import homeweatherclock.composeapp.generated.resources.setting_title_theme
import homeweatherclock.composeapp.generated.resources.setting_title_weather_api_keys
import homeweatherclock.composeapp.generated.resources.setting_title_weather_city_key
import homeweatherclock.composeapp.generated.resources.setting_title_weather_enabled
import homeweatherclock.composeapp.generated.resources.setting_title_weather_language
import homeweatherclock.composeapp.generated.resources.setting_title_weather_server
import org.jetbrains.compose.resources.stringResource
import ru.weatherclock.adg.app.domain.model.settings.SettingKey

@Composable
fun SettingKey.getName(): String = when (this) {
    SettingKey.WeatherEnabled -> stringResource(Res.string.setting_title_weather_enabled)
    SettingKey.WeatherApiKeys -> stringResource(Res.string.setting_title_weather_api_keys)
    SettingKey.WeatherCityKey1, SettingKey.WeatherCityKey2 -> stringResource(Res.string.setting_title_weather_city_key)
    SettingKey.WeatherLanguage -> stringResource(Res.string.setting_title_weather_language)
    SettingKey.TextCalendarEnabled -> stringResource(Res.string.setting_title_text_calendar_enabled)
    SettingKey.GridCalendarEnabled -> stringResource(Res.string.setting_title_grid_calendar_enabled)
    SettingKey.ProdCalendarIsRussia -> stringResource(Res.string.setting_title_prod_calendar_is_russia)
    SettingKey.ProdCalendarRussiaRegion -> stringResource(Res.string.setting_title_prod_calendar_russia_region)
    SettingKey.ProdCalendarDayDescriptionEnabled -> stringResource(Res.string.setting_title_prod_calendar_day_description_enabled)
    SettingKey.DotsFlashEnabled -> stringResource(Res.string.setting_title_dots_flash_enabled)
    SettingKey.DotsFlashAnimated -> stringResource(Res.string.setting_title_dots_flash_animated)
    SettingKey.HourlyBeepEnabled -> stringResource(Res.string.setting_title_hourly_beep_enabled)
    SettingKey.HourlyBeepHoursRange -> stringResource(Res.string.setting_title_hourly_beep_hours_range)
    SettingKey.HeaderWeatherConfig -> stringResource(Res.string.setting_title_header_weather_config)
    SettingKey.HeaderTimeConfig -> stringResource(Res.string.setting_title_header_time_config)
    SettingKey.HeaderCalendarConfig -> stringResource(Res.string.setting_title_header_calendar_config)
    SettingKey.HeaderProdCalendarConfig -> stringResource(Res.string.setting_title_header_prod_calendar_config)
    SettingKey.HeaderTheme -> stringResource(Res.string.setting_title_header_theme)
    SettingKey.Theme -> stringResource(Res.string.setting_title_theme)
    SettingKey.HideElementsByTime -> stringResource(Res.string.setting_title_hide_elements_by_time)
    SettingKey.HideElementsByTimeRange -> stringResource(Res.string.setting_title_hide_elements_by_hours_range)
    SettingKey.HideWeatherByTime -> stringResource(Res.string.setting_title_hide_weather_by_time)
    SettingKey.HideTextCalendarByTime -> stringResource(Res.string.setting_title_hide_text_calendar_by_time)
    SettingKey.HideGridCalendarByTime -> stringResource(Res.string.setting_title_hide_grid_calendar_by_time)
    SettingKey.HoursWithLeadingZero -> stringResource(Res.string.setting_title_hours_with_leading_zero)
    SettingKey.WeatherServers -> stringResource(Res.string.setting_title_weather_server)
    SettingKey.HeaderSystem -> stringResource(Res.string.setting_title_system)
    SettingKey.AutoStart -> stringResource(Res.string.setting_description_system)
}