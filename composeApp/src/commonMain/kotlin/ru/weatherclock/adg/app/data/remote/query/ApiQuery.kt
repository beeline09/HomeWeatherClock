package ru.weatherclock.adg.app.data.remote.query

interface ApiQuery {

    fun buildUrl(): String
}