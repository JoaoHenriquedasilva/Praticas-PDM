package com.sistemaestudos

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sistemaestudos.model.City
import com.sistemaestudos.model.User
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
import com.sistemaestudos.monitor.ForecastMonitor
import com.sistemaestudos.repo.Repository

class MainViewModel(
    private val repository: Repository,
    private val service: WeatherService,
    private val monitor: ForecastMonitor
) : ViewModel(), Repository.Listener {

    // Alterado o tipo interno para o User da camada Model
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _cities = mutableStateMapOf<String, City>()
    val cities: List<City>
        get() = _cities.values.toList().sortedBy { it.name }

    private val _weather = mutableStateMapOf<String, Weather>()

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
        repository.setListener(this)
    }

    override fun onUserLoaded(user: User) {
        _user.value = user
    }

    override fun onUserSignOut() {
        _user.value = null
        _cities.clear()
        _weather.clear()
        _forecast.clear()
        monitor.cancelAll()
    }

    override fun onCityAdded(city: City) {
        _cities[city.name!!] = city
        monitor.updateCity(city)
    }

    override fun onCityUpdated(city: City) {
        _cities.remove(key = city.name)
        _cities[city.name!!] = city
        monitor.updateCity(city)
    }

    override fun onCityRemoved(city: City) {
        _cities.remove(key = city.name)
        _weather.remove(key = city.name)
        _forecast.remove(key = city.name)
        monitor.cancelCity(city)
    }

    fun remove(city: City) {
        repository.remove(city)
    }

    fun addCity(name: String) {
        service.getLocation(name) { lat, lng ->
            if (lat != null && lng != null) {
                repository.add(City(name = name, location = LatLng(lat, lng)))
            }
        }
    }

    fun addCity(location: LatLng) {
        service.getName(location.latitude, location.longitude) { name ->
            if (name != null) {
                repository.add(City(name = name, location = location))
            }
        }
    }

    fun weather(name: String) = _weather.getOrPut(name) {
        loadWeather(name)
        Weather.LOADING
    }

    private fun loadWeather(name: String) {
        service.getWeather(name) { apiWeather ->
            apiWeather?.let {
                _weather[name] = apiWeather.toWeather()
                loadBitmap(name)
            }
        }
    }

    private fun loadBitmap(name: String) {
        _weather[name]?.let { weather ->
            service.getBitmap(weather.imgUrl) { bitmap ->
                if (bitmap != null) {
                    _weather[name] = weather.copy(bitmap = bitmap)
                }
            }
        }
    }

    fun forecast(name: String) = _forecast.getOrPut(name) {
        loadForecast(name)
        emptyList()
    }

    private fun loadForecast(name: String) {
        service.getForecast(name) { apiForecast ->
            apiForecast?.toForecast()?.let {
                _forecast[name] = it
            }
        }
    }

    fun update(city: City) {
        repository.update(city)
    }

    override fun onCleared() {
        super.onCleared()
        repository.setListener(null)
    }
}

class MainViewModelFactory(
    private val repository: Repository, // <-- Alterado aqui
    private val service: WeatherService,
    private val monitor: ForecastMonitor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, service, monitor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}