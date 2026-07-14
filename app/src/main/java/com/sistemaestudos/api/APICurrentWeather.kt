package com.sistemaestudos.api

import com.sistemaestudos.model.Weather

data class APICurrentWeather(
    var location: APILocation? = null,
    var current: APIWeather? = null
)

// Função de extensão para converter APIWeather em Weather
fun APIWeather.toWeather(): Weather {
    return Weather(
        date = this.last_updated ?: "...",
        desc = this.condition?.text ?: "...",
        temp = this.temp_c?.toString() ?: "--",
        imgUrl = "https:" + (this.condition?.icon ?: "")
    )
}