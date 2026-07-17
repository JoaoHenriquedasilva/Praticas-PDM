package com.sistemaestudos.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// PASSO 1: Recebe o Context do Android no construtor
class WeatherService(private val context: Context) {

    private val weatherAPI: WeatherServiceAPI

    // PASSO 1: ImageLoader do Coil configurado com allowHardware(false)
    private val imageLoader = ImageLoader.Builder(context)
        .allowHardware(false)
        .build()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(WeatherServiceAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherAPI = retrofit.create(WeatherServiceAPI::class.java)
    }

    fun getLocation(name: String, onResponse: (Double?, Double?) -> Unit) {
        weatherAPI.getLocation(name).enqueue(object : Callback<List<APILocation>> {
            override fun onResponse(call: Call<List<APILocation>>, response: Response<List<APILocation>>) {
                if (response.isSuccessful) {
                    val location = response.body()?.firstOrNull()
                    onResponse(location?.lat, location?.lon)
                } else {
                    onResponse(null, null)
                }
            }

            override fun onFailure(call: Call<List<APILocation>>, t: Throwable) {
                onResponse(null, null)
            }
        })
    }

    fun getName(lat: Double, lng: Double, onResponse: (String?) -> Unit) {
        weatherAPI.getName("$lat,$lng").enqueue(object : Callback<List<APILocation>> {
            override fun onResponse(call: Call<List<APILocation>>, response: Response<List<APILocation>>) {
                if (response.isSuccessful) {
                    val location = response.body()?.firstOrNull()
                    onResponse(location?.name)
                } else {
                    onResponse(null)
                }
            }

            override fun onFailure(call: Call<List<APILocation>>, t: Throwable) {
                onResponse(null)
            }
        })
    }

    fun getWeather(name: String, onResponse: (APIWeather?) -> Unit) {
        weatherAPI.getWeather(name).enqueue(object : Callback<APICurrentWeather> {
            override fun onResponse(call: Call<APICurrentWeather>, response: Response<APICurrentWeather>) {
                if (response.isSuccessful) {
                    onResponse(response.body()?.current)
                } else {
                    onResponse(null)
                }
            }

            override fun onFailure(call: Call<APICurrentWeather>, t: Throwable) {
                onResponse(null)
            }
        })
    }

    fun getForecast(name: String, onResponse: (APIWeatherForecast?) -> Unit) {
        weatherAPI.getForecast(name).enqueue(object : Callback<APIWeatherForecast> {
            override fun onResponse(call: Call<APIWeatherForecast>, response: Response<APIWeatherForecast>) {
                if (response.isSuccessful) {
                    onResponse(response.body())
                } else {
                    onResponse(null)
                }
            }

            override fun onFailure(call: Call<APIWeatherForecast>, t: Throwable) {
                onResponse(null)
            }
        })
    }

    // PASSO 1 (Parte 2): Baixa a imagem da URL e converte para Bitmap via Coil
    fun getBitmap(imgUrl: String, onResponse: (Bitmap?) -> Unit) {
        val request = ImageRequest.Builder(context)
            .data(imgUrl)
            .allowHardware(false)
            .target(
                onSuccess = { drawable ->
                    val bitmap = (drawable as BitmapDrawable).bitmap
                    onResponse(bitmap)
                },
                onError = {
                    onResponse(null)
                }
            )
            .build()

        imageLoader.enqueue(request)
    }
}