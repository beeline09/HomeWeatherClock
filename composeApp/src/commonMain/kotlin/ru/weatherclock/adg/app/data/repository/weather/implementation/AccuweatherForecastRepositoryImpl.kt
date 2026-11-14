package ru.weatherclock.adg.app.data.repository.weather.implementation

import ru.weatherclock.adg.app.data.dto.forecast.accuweather.AccuweatherForecastDto
import ru.weatherclock.adg.app.data.remote.service.implementation.AccuweatherKtorServiceImpl
import ru.weatherclock.adg.app.data.repository.weather.AccuweatherForecastRepository

class AccuweatherForecastRepositoryImpl(private val ktorService: AccuweatherKtorServiceImpl) :
    AccuweatherForecastRepository {

    override suspend fun getWeatherForecast(
        cityKey: String,
        apiKeys: List<String>,
        language: String
    ): AccuweatherForecastDto = ktorService.getWeatherForecast(
        cityKey,
        apiKeys,
        language
    )

}
