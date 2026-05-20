package com.sistemaestudos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.sistemaestudos.MainViewModel

@Composable
fun MapPage(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val camPosState = rememberCameraPositionState ()
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = camPosState,
        onMapClick = { latLng ->
            viewModel.add("Cidade@${latLng.latitude}:${latLng.longitude}")
        }
    ) {
        viewModel.cities.forEach { city ->
            if (city.location != null) {
                Marker(
                    state = rememberMarkerState(position = city.location),
                    title = city.name,
                    snippet = "${city.location}"
                )
            }
        }
    }
}