package com.sistemaestudos

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sistemaestudos.db.fb.FBCity
import com.sistemaestudos.db.fb.FBDatabase
import com.sistemaestudos.db.fb.FBUser
import com.sistemaestudos.db.fb.toFBCity
import com.sistemaestudos.model.City
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.sistemaestudos.api.WeatherService

class MainViewModel(
    private val db: FBDatabase,
    private val service: WeatherService
) : ViewModel(), FBDatabase.Listener {

    private val _user = MutableStateFlow<FBUser?>(null)
    val user: StateFlow<FBUser?> = _user.asStateFlow()

    val cities = mutableStateListOf<FBCity>()

    init {
        db.setListener(this)
    }

    override fun onUserLoaded(user: FBUser) {
        _user.value = user
    }

    override fun onUserSignOut() {
        _user.value = null
        cities.clear()
    }

    override fun onCityAdded(city: FBCity) {
        cities.add(city)
    }

    override fun onCityUpdated(city: FBCity) {
        val index = cities.indexOfFirst { it.name == city.name }
        if (index >= 0) {
            cities[index] = city
        }
    }

    override fun onCityRemoved(city: FBCity) {
        cities.removeIf { it.name == city.name }
    }

    fun remove(city: City) {
        db.remove(city.toFBCity())
    }

    fun add(name: String) {
        service.getLocation(name) { lat, lng ->
            if (lat != null && lng != null) {
                val novaCidade = FBCity().apply {
                    this.name = name
                    this.lat = lat
                    this.lng = lng
                }
                db.add(novaCidade)
            }
        }
    }

    fun add(location: LatLng) {
        service.getName(location.latitude, location.longitude) { name ->
            if (name != null) {
                val novaCidadeComLoc = FBCity().apply {
                    this.name = name
                    this.lat = location.latitude
                    this.lng = location.longitude
                }
                db.add(novaCidadeComLoc)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        db.setListener(null)
    }
    fun addCity(name: String) {
        service.getLocation(name) { lat, lng ->
            if (lat != null && lng != null) {
                db.add(City(name=name, location=LatLng(lat, lng)).toFBCity())
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