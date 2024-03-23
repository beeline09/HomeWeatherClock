package ru.weatherclock.adg.app.presentation.screens.settings.components.utils

import androidx.compose.runtime.Composable
import homeweatherclock.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ru.weatherclock.adg.app.domain.model.settings.SettingKey

@Composable
fun SettingKey.getDescription(): String = when (this) {
    SettingKey.WeatherApiKeysAccuweather -> stringResource(Res.string.setting_description_weather_api_keys_accuweather)
    SettingKey.WeatherApiKeysOpenweathermap -> stringResource(Res.string.setting_description_weather_api_keys_openweathermap)
    SettingKey.WeatherCityKey1 -> stringResource(Res.string.setting_description_weather_city_key_1)
    SettingKey.WeatherCityKey2 -> stringResource(Res.string.setting_description_weather_city_key_2)
    SettingKey.WeatherLanguage -> stringResource(Res.string.setting_description_weather_language)
    SettingKey.TextCalendarEnabled -> stringResource(Res.string.setting_description_text_calendar_enabled)
    SettingKey.GridCalendarEnabled -> stringResource(Res.string.setting_description_grid_calendar_enabled)
    SettingKey.ProdCalendarIsRussia -> stringResource(Res.string.setting_description_prod_calendar_is_russia)
    SettingKey.ProdCalendarRussiaRegion -> stringResource(Res.string.setting_description_prod_calendar_russia_region)
    SettingKey.ProdCalendarDayDescriptionEnabled -> stringResource(Res.string.setting_description_prod_calendar_day_description_enabled)
    SettingKey.DotsFlashAnimated -> stringResource(Res.string.setting_description_dots_flash_animated)
    SettingKey.HourlyBeepEnabled -> stringResource(Res.string.setting_description_hourly_beep_enabled)
    SettingKey.HideElementsByTime -> stringResource(Res.string.setting_description_hide_elements_by_time)
    SettingKey.HoursWithLeadingZero -> stringResource(Res.string.setting_description_hours_with_leading_zero)
    SettingKey.WeatherServers -> stringResource(Res.string.setting_description_weather_server)
    else -> ""
}