package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.CoroutineDispatcher
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import ru.weatherclock.adg.db.AirAndPollen
import ru.weatherclock.adg.db.Database
import ru.weatherclock.adg.db.DegreeDaySummary
import ru.weatherclock.adg.db.Evapotranspiration
import ru.weatherclock.adg.db.ForecastDetail
import ru.weatherclock.adg.db.ForecastHeadline
import ru.weatherclock.adg.db.Ice
import ru.weatherclock.adg.db.Moon
import ru.weatherclock.adg.db.ProdCalendar
import ru.weatherclock.adg.db.Rain
import ru.weatherclock.adg.db.RealFeelTemperature
import ru.weatherclock.adg.db.RealFeelTemperatureShade
import ru.weatherclock.adg.db.Snow
import ru.weatherclock.adg.db.SolarIrradiance
import ru.weatherclock.adg.db.Temperature
import ru.weatherclock.adg.db.TotalLiquid
import ru.weatherclock.adg.db.Wind
import ru.weatherclock.adg.db.WindGust

expect fun createDriver(): SqlDriver

private val intAdapter = object: ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()

    override fun encode(value: Int): Long = value.toLong()
}

fun createDatabase(): Database {
    return Database(
        driver = createDriver(),
        ProdCalendarAdapter = ProdCalendar.Adapter(
            type_idAdapter = intAdapter,
            regionAdapter = intAdapter,
            working_hoursAdapter = intAdapter
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

expect fun platformModule(): Module

expect val ioDispatcher: CoroutineDispatcher

expect val separatorChar: String

expect val systemLocale: String