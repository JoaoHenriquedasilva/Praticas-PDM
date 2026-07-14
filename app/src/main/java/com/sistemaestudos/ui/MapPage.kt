package com.sistemaestudos.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.sistemaestudos.MainViewModel
import com.sistemaestudos.R
import com.sistemaestudos.model.Weather

@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val recife = LatLng(-8.04756, -34.877)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(recife, 10f)
    }

    Column(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            viewModel.cities.forEach { city ->
                // Atribui a uma variável local 'location' para garantir o smart cast seguro
                val location = city.location
                if (location != null) {
                    val weather = viewModel.weather(city.name)

                    val image = weather.bitmap ?: getDrawable(context, R.drawable.loading)!!.toBitmap()

                    val marker = BitmapDescriptorFactory
                        .fromBitmap(image.scale(120, 120))

                    val desc = if (weather == Weather.LOADING)
                        "Carregando clima..." else weather.desc

                    Marker(
                        state = MarkerState(position = location),
                        icon = marker,
                        title = city.name,
                        snippet = desc
                    )
                }
            }
        }
    }
}