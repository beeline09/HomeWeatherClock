package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.CoroutineDispatcher
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import org.koin.core.module.Module
import ru.weatherclock.adg.db.Accuweather.AirAndPollen
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
import ru.weatherclock.adg.db.Accuweather.Temperature
import ru.weatherclock.adg.db.Accuweather.TotalLiquid
import ru.weatherclock.adg.db.Accuweather.Wind
import ru.weatherclock.adg.db.Accuweather.WindGust
import ru.weatherclock.adg.db.AccuweatherDb
import ru.weatherclock.adg.db.OpenWeatherMap.ForecastItem
import ru.weatherclock.adg.db.OpenWeatherMap.MainInfo
import ru.weatherclock.adg.db.OpenWeatherMap.WeatherIcon
import ru.weatherclock.adg.db.OpenWeatherMapDb
import ru.weatherclock.adg.db.ProdCalendar.ProdCalendar
import ru.weatherclock.adg.db.ProdCalendarDb

expect fun createDbDriver(
    dbName: String,
    schema: SqlSchema<QueryResult.Value<Unit>>
): SqlDriver

private val intAdapter = object: ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()

    override fun encode(value: Int): Long = value.toLong()
}

fun createAccuweatherDb(): AccuweatherDb {
    return AccuweatherDb(
        driver = createDbDriver(
            schema = AccuweatherDb.Schema,
            dbName = "accuweather.db"
        ),
        ForecastHeadlineAdapter = ForecastHeadline.Adapter(
            intAdapter,
        ),
        AirAndPollenAdapter = AirAndPollen.Adapter(
            intAdapter,
            intAdapter
        ),
        DegreeDaySummaryAdapter = DegreeDaySummary.Adapter(
            intAdapter,
            intAdapter
        ),
        EvapotranspirationAdapter = Evapotranspiration.Adapter(intAdapter),
        ForecastDetailAdapter = ForecastDetail.Adapter(
            intAdapter,
            intAdapter,
            intAdapter,
            intAdapter,
            intAdapter,
            intAdapter,
            intAdapter
        ),
        IceAdapter = Ice.Adapter(intAdapter),
        SnowAdapter = Snow.Adapter(intAdapter),
        RainAdapter = Rain.Adapter(intAdapter),
        SolarIrradianceAdapter = SolarIrradiance.Adapter(intAdapter),
        TemperatureAdapter = Temperature.Adapter(
            intAdapter,
            intAdapter
        ),
        RealFeelTemperatureAdapter = RealFeelTemperature.Adapter(
            intAdapter,
            intAdapter
        ),
        RealFeelTemperatureShadeAdapter = RealFeelTemperatureShade.Adapter(
            intAdapter,
            intAdapter
        ),
        MoonAdapter = Moon.Adapter(intAdapter),
        WindAdapter = Wind.Adapter(intAdapter),
        WindGustAdapter = WindGust.Adapter(intAdapter),
        TotalLiquidAdapter = TotalLiquid.Adapter(intAdapter)
    )
}

fun createProdCalendarDb(): ProdCalendarDb {
    return ProdCalendarDb(
        driver = createDbDriver(
            schema = ProdCalendarDb.Schema,
            dbName = "prod_calendar.db"
        ),
        ProdCalendarAdapter = ProdCalendar.Adapter(
            type_idAdapter = intAdapter,
            regionAdapter = intAdapter,
            working_hoursAdapter = intAdapter
        ),
    )
}

fun createOpenWeatherMapDb(): OpenWeatherMapDb {
    return OpenWeatherMapDb(
        driver = createDbDriver(
            schema = OpenWeatherMapDb.Schema,
            dbName = "open_weather_map.db"
        ),
        ForecastItemAdapter = ForecastItem.Adapter(
            part_of_dayAdapter = intAdapter,
            visibilityAdapter = intAdapter,
            cloudsAdapter = intAdapter
        ),
        MainInfoAdapter = MainInfo.Adapter(
            pressureAdapter = intAdapter,
            seaLevelAdapter = intAdapter,
            ground_levelAdapter = intAdapter,
            humidityAdapter = intAdapter
        ),
        WeatherIconAdapter = WeatherIcon.Adapter(
            idAdapter = intAdapter
        ),
        WindAdapter = ru.weatherclock.adg.db.OpenWeatherMap.Wind.Adapter(
            degreeAdapter = intAdapter
        )
    )
}

expect fun platformModule(): Module

expect val ioDispatcher: CoroutineDispatcher

expect val separatorChar: String

expect val systemLocale: String