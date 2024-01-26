package ru.weatherclock.adg.app.data.query.base

interface ApiQuery {

    fun buildUrl(): String
}