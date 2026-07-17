package com.sistemaestudos.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServiceAPI {

    @GET("search.json?key=$API_KEY")
    fun getLocation(@Query("q") name: String): Call<List<APILocation>>

    @GET("search.json?key=$API_KEY")
    fun getName(@Query("q") latLng: String): Call<List<APILocation>>

    @GET("current.json?key=$API_KEY")
    fun getWeather(@Query("q") name: String): Call<APICurrentWeather>

    @GET("forecast.json?key=$API_KEY&days=10")
    fun getForecast(@Query("q") name: String): Call<APIWeatherForecast>

    companion object {
        const val API_KEY = "3ba4df4580374d078f4211914261306"
        const val BASE_URL = "https://api.weatherapi.com/v1/"
    }
}