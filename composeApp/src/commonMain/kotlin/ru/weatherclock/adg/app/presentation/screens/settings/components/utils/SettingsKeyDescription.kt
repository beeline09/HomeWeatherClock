package ru.weatherclock.adg.app.presentation.screens.settings.components.utils

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import ru.weatherclock.adg.MR
import ru.weatherclock.adg.app.domain.model.settings.SettingKey

@Composable
fun SettingKey.getDescription(): String = when (this) {
    SettingKey.WeatherApiKeys -> stringResource(MR.strings.setting_description_weather_api_keys)
    SettingKey.WeatherCityKey1 -> stringResource(MR.strings.setting_description_weather_city_key_1)
    SettingKey.WeatherCityKey2 -> stringResource(MR.strings.setting_description_weather_city_key_2)
    SettingKey.WeatherLanguage -> stringResource(MR.strings.setting_description_weather_language)
    SettingKey.TextCalendarEnabled -> stringResource(MR.strings.setting_description_text_calendar_enabled)
    SettingKey.GridCalendarEnabled -> stringResource(MR.strings.setting_description_grid_calendar_enabled)
    SettingKey.ProdCalendarIsRussia -> stringResource(MR.strings.setting_description_prod_calendar_is_russia)
    SettingKey.ProdCalendarRussiaRegion -> stringResource(MR.strings.setting_description_prod_calendar_russia_region)
    SettingKey.ProdCalendarDayDescriptionEnabled -> stringResource(MR.strings.setting_description_prod_calendar_day_description_enabled)
    SettingKey.DotsFlashAnimated -> stringResource(MR.strings.setting_description_dots_flash_animated)
    SettingKey.HourlyBeepEnabled -> stringResource(MR.strings.setting_description_hourly_beep_enabled)
    SettingKey.HideElementsByTime -> stringResource(MR.strings.setting_description_hide_elements_by_time)
    SettingKey.HoursWithLeadingZero -> stringResource(MR.strings.setting_description_hours_with_leading_zero)
    else -> ""
}