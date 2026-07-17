package com.sistemaestudos.db.fb

import com.google.android.gms.maps.model.LatLng
import com.sistemaestudos.model.City

data class FBCity(
    var name: String? = null,
    var lat: Double? = null,
    var lng: Double? = null,
    var monitored: Boolean = false
) {
    fun toCity(): City {
        val loc = if (lat != null && lng != null) LatLng(lat!!, lng!!) else null
        return City(name!!, loc, monitored)
    }
}

fun City.toFBCity(): FBCity {
    val lat = location?.latitude
    val lng = location?.longitude
    return FBCity(name, lat, lng, isMonitored)
}
