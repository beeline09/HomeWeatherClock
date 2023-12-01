package ru.weatherclock.adg.app.domain.usecase

import kotlinx.coroutines.flow.flow
import ru.weatherclock.adg.app.data.dto.asDomainModel
import ru.weatherclock.adg.app.data.repository.AbstractRepository
import ru.weatherclock.adg.app.domain.model.Forecast

class GetForecastUseCase(private val repository: AbstractRepository) {

    private var forecast: Forecast? = null
    private var hasMorePages = true
    operator fun invoke() = flow {
        if (hasMorePages) {
            val response = repository.getWeatherForecast("").asDomainModel()
            forecast = response
            emit(response)
        }
    }
}
