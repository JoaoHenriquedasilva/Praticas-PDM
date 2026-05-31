package com.sistemaestudos

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.sistemaestudos.model.City
import com.sistemaestudos.model.User

class MainViewModel : ViewModel() {
    private val _cities = getCities().toMutableStateList()
    val cities: List<City>
        get() = _cities

    private val _user = mutableStateOf<User?> (null)
    val user : User?
        get() = _user.value

    fun remove(city: City) {
        _cities.remove(city)
    }
    fun add(name: String) {
        if (name.contains("@") && name.contains(":")) {
            try {

                val partes = name.split("@")
                val coordenadas = partes[1].split(":")
                val lat = coordenadas[0].toDouble()
                val lng = coordenadas[1].toDouble()

                val novaCidade = City(
                    name = "Ponto Marcado #${_cities.size + 1}",
                    location = LatLng(lat, lng),
                    weather = "Carregando..."
                )
                _cities.add(novaCidade)
            } catch (e: Exception) {
                _cities.add(City(name = name, weather = "Carregando..."))
            }
        } else {
            _cities.add(City(name = name, weather = "Carregando..."))
        }
    }

    fun add(name: String, location: LatLng? = null) {
        _cities.add(City(name = name, location = location, weather = "Carregando..."))
    }
}

private fun getCities() = List(20) { i ->
    City(name = "Cidade $i", weather = "Carregando clima...")
}