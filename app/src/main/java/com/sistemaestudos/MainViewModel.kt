package com.sistemaestudos

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sistemaestudos.db.fb.FBCity
import com.sistemaestudos.db.fb.FBDatabase
import com.sistemaestudos.db.fb.FBUser
import com.sistemaestudos.db.fb.toFBCity
import com.sistemaestudos.model.City
import com.sistemaestudos.model.Weather
import com.sistemaestudos.model.Forecast
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.sistemaestudos.api.WeatherService
import com.sistemaestudos.api.toWeather
import com.sistemaestudos.api.toForecast
import com.sistemaestudos.ui.nav.Route

class MainViewModel(
    private val db: FBDatabase,
    private val service: WeatherService
) : ViewModel(), FBDatabase.Listener {

    private val _user = MutableStateFlow<FBUser?>(null)
    val user: StateFlow<FBUser?> = _user.asStateFlow()

    // Cidades do Firebase
    private val _cities = mutableStateMapOf<String, City>()
    val cities: List<City>
        get() = _cities.values.toList().sortedBy { it.name }

    // Clima atual
    private val _weather = mutableStateMapOf<String, Weather>()

    // Passo 5: Previsão do tempo para 10 dias
    private val _forecast = mutableStateMapOf<String, List<Forecast>?>()

    private var _city = mutableStateOf<String?>(null)
    var city: String?
        get() = _city.value
        set(tmp) { _city.value = tmp }
    private var _page = mutableStateOf<Route>(Route.Home)
    var page: Route
        get() = _page.value
        set(tmp) { _page.value = tmp }

    init {
        db.setListener(this)
    }

    override fun onUserLoaded(user: FBUser) {
        _user.value = user
    }

    override fun onUserSignOut() {
        _user.value = null
        _cities.clear()
        _weather.clear()
        _forecast.clear()
    }

    override fun onCityAdded(city: FBCity) {
        _cities[city.name!!] = city.toCity()
    }

    override fun onCityUpdated(city: FBCity) {
        _cities.remove(city.name)
        _cities[city.name!!] = city.toCity()
    }

    override fun onCityRemoved(city: FBCity) {
        _cities.remove(city.name)
        _weather.remove(city.name)
        _forecast.remove(city.name)
    }

    fun remove(city: City) {
        db.remove(city.toFBCity())
    }

    fun addCity(name: String) {
        service.getLocation(name) { lat, lng ->
            if (lat != null && lng != null) {
                db.add(City(name = name, location = LatLng(lat, lng)).toFBCity())
            }
        }
    }

    fun addCity(location: LatLng) {
        service.getName(location.latitude, location.longitude) { name ->
            if (name != null) {
                db.add(City(name = name, location = location).toFBCity())
            }
        }
    }

    // Parte 1: Clima atual
    fun weather(name: String) = _weather.getOrPut(name) {
        loadWeather(name)
        Weather.LOADING
    }

    private fun loadWeather(name: String) {
        service.getWeather(name) { apiWeather ->
            apiWeather?.let {
                _weather[name] = apiWeather.toWeather()
            }
        }
    }

    fun forecast(name: String) = _forecast.getOrPut(name) {
        loadForecast(name)
        emptyList() // return
    }

    private fun loadForecast(name: String) {
        service.getForecast(name) { apiForecast ->
            apiForecast?.toForecast()?.let {
                _forecast[name] = it
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        db.setListener(null)
    }
}

class MainViewModelFactory(
    private val db: FBDatabase,
    private val service: WeatherService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(db, service) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}