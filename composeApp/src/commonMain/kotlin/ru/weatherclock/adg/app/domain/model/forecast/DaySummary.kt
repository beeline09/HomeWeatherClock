package ru.weatherclock.adg.app.domain.model.forecast

data class DaySummary(
    val heating: UnitInfo,
    val cooling: UnitInfo
)