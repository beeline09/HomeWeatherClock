package ru.weatherclock.adg.app.domain.usecase

import kotlinx.coroutines.flow.flow
import ru.weatherclock.adg.app.data.dto.asDomainModel
import ru.weatherclock.adg.app.data.repository.AbstractRepository
import ru.weatherclock.adg.app.domain.model.Forecast

class GetForecastUseCase(private val repository: AbstractRepository) {

    private var forecast: Forecast? = null
    operator fun invoke() = flow {
        val response = repository.getWeatherForecast("291658").asDomainModel()
        forecast = response
        emit(response)
    }
}
