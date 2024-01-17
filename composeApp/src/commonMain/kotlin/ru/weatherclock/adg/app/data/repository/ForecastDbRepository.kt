package ru.weatherclock.adg.app.data.repository

import kotlinx.datetime.LocalDate
import ru.weatherclock.adg.app.domain.model.forecast.DetailType
import ru.weatherclock.adg.db.AirAndPollen
import ru.weatherclock.adg.db.DailyForecast
import ru.weatherclock.adg.db.DegreeDaySummary
import ru.weatherclock.adg.db.Evapotranspiration
import ru.weatherclock.adg.db.ForecastDetail
import ru.weatherclock.adg.db.ForecastHeadline
import ru.weatherclock.adg.db.Ice
import ru.weatherclock.adg.db.Moon
import ru.weatherclock.adg.db.Rain
import ru.weatherclock.adg.db.RealFeelTemperature
import ru.weatherclock.adg.db.RealFeelTemperatureShade
import ru.weatherclock.adg.db.Snow
import ru.weatherclock.adg.db.SolarIrradiance
import ru.weatherclock.adg.db.Sun
import ru.weatherclock.adg.db.Temperature
import ru.weatherclock.adg.db.TotalLiquid
import ru.weatherclock.adg.db.Wind
import ru.weatherclock.adg.db.WindGust

abstract class ForecastDbRepository {

    abstract suspend fun insertHeadline(headline: ForecastHeadline)
    abstract suspend fun insertDailyForecast(dailyForecast: DailyForecast): Long
    abstract suspend fun insertDailyForecast(dailyForecasts: List<DailyForecast>): List<Long>
    abstract suspend fun insertAirAndPollen(airAndPollens: List<AirAndPollen>): List<Long>
    abstract suspend fun insertAirAndPollen(airAndPollen: AirAndPollen): Long
    abstract suspend fun insertDegreeDaySummary(
        forecastPid: Long,
        degreeDaySummary: DegreeDaySummary
    ): Long

    abstract suspend fun insertTemperature(
        forecastPid: Long,
        temperature: Temperature
    ): Long

    abstract suspend fun insertRealFeelTemperature(
        forecastPid: Long,
        realFeelTemperature: RealFeelTemperature
    ): Long

    abstract suspend fun insertRealFeelTemperatureShade(
        forecastPid: Long,
        realFeelTemperatureShade: RealFeelTemperatureShade
    ): Long

    abstract suspend fun insertDetail(
        forecastPid: Long,
        forecastDetail: ForecastDetail,
        type: DetailType
    ): Long

    abstract suspend fun insertEvapotranspiration(
        detailPid: Long,
        evapotranspiration: Evapotranspiration
    ): Long

    abstract suspend fun insertSolarIrradiance(
        detailPid: Long,
        solarIrradiance: SolarIrradiance
    ): Long

    abstract suspend fun insertWind(
        detailPid: Long,
        wind: Wind
    ): Long

    abstract suspend fun insertWindGust(
        detailPid: Long,
        windGust: WindGust
    ): Long

    abstract suspend fun insertTotalLiquid(
        detailPid: Long,
        totalLiquid: TotalLiquid
    ): Long

    abstract suspend fun insertSnow(
        detailPid: Long,
        snow: Snow
    ): Long

    abstract suspend fun insertRain(
        detailPid: Long,
        rain: Rain
    ): Long

    abstract suspend fun insertIce(
        detailPid: Long,
        ice: Ice
    ): Long

    abstract suspend fun insertMoon(
        forecastPid: Long,
        moon: Moon
    ): Long

    abstract suspend fun insertSun(
        forecastPid: Long,
        sun: Sun
    ): Long

    abstract suspend fun getHeadline(
        forecastKey: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): ForecastHeadline?

    abstract suspend fun getForecast(
        forecastKey: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DailyForecast>

    abstract suspend fun getAirAndPollen(byForecastPid: Long): List<AirAndPollen>
    abstract suspend fun getDegreeDaySummary(byForecastPid: Long): DegreeDaySummary?
    abstract suspend fun getTemperature(byForecastPid: Long): Temperature?
    abstract suspend fun getRealFeelTemperature(byForecastPid: Long): RealFeelTemperature?
    abstract suspend fun getRealFeelTemperatureShade(byForecastPid: Long): RealFeelTemperatureShade?
    abstract suspend fun getDetail(
        byForecastPid: Long,
        type: DetailType
    ): ForecastDetail?

    abstract suspend fun getEvapotranspiration(byDetailPid: Long): Evapotranspiration?
    abstract suspend fun getSolarIrradiance(byDetailPid: Long): SolarIrradiance?
    abstract suspend fun getWind(byDetailPid: Long): Wind?
    abstract suspend fun getWindGust(byDetailPid: Long): WindGust?
    abstract suspend fun getTotalLiquid(byDetailPid: Long): TotalLiquid?
    abstract suspend fun getSnow(byDetailPid: Long): Snow?
    abstract suspend fun getRain(byDetailPid: Long): Rain?
    abstract suspend fun getIce(byDetailPid: Long): Ice?
    abstract suspend fun getMoon(byForecastPid: Long): Moon?
    abstract suspend fun getSun(byForecastPid: Long): Sun?

}