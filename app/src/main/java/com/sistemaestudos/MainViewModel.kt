package com.sistemaestudos

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sistemaestudos.db.fb.FBCity
import com.sistemaestudos.db.fb.FBDatabase
import com.sistemaestudos.db.fb.FBUser
import com.sistemaestudos.db.fb.toFBCity
import com.sistemaestudos.model.City
import com.sistemaestudos.model.Weather
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.sistemaestudos.api.WeatherService
import com.sistemaestudos.api.toWeather

class MainViewModel(
    private val db: FBDatabase,
    private val service: WeatherService
) : ViewModel(), FBDatabase.Listener {

    private val _user = MutableStateFlow<FBUser?>(null)
    val user: StateFlow<FBUser?> = _user.asStateFlow()

    // PASSO 7: Declarando o Map e a propriedade calculada 'cities'
    private val _cities = mutableStateMapOf<String, City>()
    val cities: List<City>
        get() = _cities.values.toList().sortedBy { it.name }

    // PASSO 7: Declarando o Map de Clima
    private val _weather = mutableStateMapOf<String, Weather>()

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
    }

    // PASSO 7: Tratadores ajustados conforme a imagem do PDF
    override fun onCityAdded(city: FBCity) {
        _cities[city.name!!] = city.toCity()
    }

    override fun onCityUpdated(city: FBCity) {
        _cities.remove(city.name)
        _cities[city.name!!] = city.toCity()
    }

    override fun onCityRemoved(city: FBCity) {
        _cities.remove(city.name)
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

    override fun onCleared() {
        super.onCleared()
        db.setListener(null)
    }
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