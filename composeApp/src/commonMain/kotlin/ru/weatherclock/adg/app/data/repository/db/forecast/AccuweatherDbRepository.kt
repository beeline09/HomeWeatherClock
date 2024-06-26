package ru.weatherclock.adg.app.data.repository.db.forecast

import kotlinx.datetime.LocalDate
import ru.weatherclock.adg.app.domain.model.forecast.DetailType
import ru.weatherclock.adg.db.Accuweather.AirAndPollen
import ru.weatherclock.adg.db.Accuweather.DailyForecast
import ru.weatherclock.adg.db.Accuweather.DegreeDaySummary
import ru.weatherclock.adg.db.Accuweather.Evapotranspiration
import ru.weatherclock.adg.db.Accuweather.ForecastDetail
import ru.weatherclock.adg.db.Accuweather.ForecastHeadline
import ru.weatherclock.adg.db.Accuweather.Ice
import ru.weatherclock.adg.db.Accuweather.Moon
import ru.weatherclock.adg.db.Accuweather.Rain
import ru.weatherclock.adg.db.Accuweather.RealFeelTemperature
import ru.weatherclock.adg.db.Accuweather.RealFeelTemperatureShade
import ru.weatherclock.adg.db.Accuweather.Snow
import ru.weatherclock.adg.db.Accuweather.SolarIrradiance
import ru.weatherclock.adg.db.Accuweather.Sun
import ru.weatherclock.adg.db.Accuweather.Temperature
import ru.weatherclock.adg.db.Accuweather.TotalLiquid
import ru.weatherclock.adg.db.Accuweather.Wind
import ru.weatherclock.adg.db.Accuweather.WindGust

interface AccuweatherDbRepository {

    suspend fun insertHeadline(headline: ForecastHeadline)
    suspend fun insertDailyForecast(dailyForecast: DailyForecast): Long
    suspend fun insertDailyForecast(dailyForecasts: List<DailyForecast>): List<Long>
    suspend fun insertAirAndPollen(airAndPollens: List<AirAndPollen>): List<Long>
    suspend fun insertAirAndPollen(airAndPollen: AirAndPollen): Long
    suspend fun insertDegreeDaySummary(
        forecastPid: Long,
        degreeDaySummary: DegreeDaySummary
    ): Long

    suspend fun insertTemperature(
        forecastPid: Long,
        temperature: Temperature
    ): Long

    suspend fun insertRealFeelTemperature(
        forecastPid: Long,
        realFeelTemperature: RealFeelTemperature
    ): Long

    suspend fun insertRealFeelTemperatureShade(
        forecastPid: Long,
        realFeelTemperatureShade: RealFeelTemperatureShade
    ): Long

    suspend fun insertDetail(
        forecastPid: Long,
        forecastDetail: ForecastDetail,
        type: DetailType
    ): Long

    suspend fun insertEvapotranspiration(
        detailPid: Long,
        evapotranspiration: Evapotranspiration
    ): Long

    suspend fun insertSolarIrradiance(
        detailPid: Long,
        solarIrradiance: SolarIrradiance
    ): Long

    suspend fun insertWind(
        detailPid: Long,
        wind: Wind
    ): Long

    suspend fun insertWindGust(
        detailPid: Long,
        windGust: WindGust
    ): Long

    suspend fun insertTotalLiquid(
        detailPid: Long,
        totalLiquid: TotalLiquid
    ): Long

    suspend fun insertSnow(
        detailPid: Long,
        snow: Snow
    ): Long

    suspend fun insertRain(
        detailPid: Long,
        rain: Rain
    ): Long

    suspend fun insertIce(
        detailPid: Long,
        ice: Ice
    ): Long

    suspend fun insertMoon(
        forecastPid: Long,
        moon: Moon
    ): Long

    suspend fun insertSun(
        forecastPid: Long,
        sun: Sun
    ): Long

    suspend fun getHeadline(
        forecastKey: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): ForecastHeadline?

    suspend fun getForecast(
        forecastKey: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DailyForecast>

    suspend fun getAirAndPollen(byForecastPid: Long): List<AirAndPollen>
    suspend fun getDegreeDaySummary(byForecastPid: Long): DegreeDaySummary?
    suspend fun getTemperature(byForecastPid: Long): Temperature?
    suspend fun getRealFeelTemperature(byForecastPid: Long): RealFeelTemperature?
    suspend fun getRealFeelTemperatureShade(byForecastPid: Long): RealFeelTemperatureShade?
    suspend fun getDetail(
        byForecastPid: Long,
        type: DetailType
    ): ForecastDetail?

    suspend fun getEvapotranspiration(byDetailPid: Long): Evapotranspiration?
    suspend fun getSolarIrradiance(byDetailPid: Long): SolarIrradiance?
    suspend fun getWind(byDetailPid: Long): Wind?
    suspend fun getWindGust(byDetailPid: Long): WindGust?
    suspend fun getTotalLiquid(byDetailPid: Long): TotalLiquid?
    suspend fun getSnow(byDetailPid: Long): Snow?
    suspend fun getRain(byDetailPid: Long): Rain?
    suspend fun getIce(byDetailPid: Long): Ice?
    suspend fun getMoon(byForecastPid: Long): Moon?
    suspend fun getSun(byForecastPid: Long): Sun?

}