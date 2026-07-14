package com.sistemaestudos.ui

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sistemaestudos.MainViewModel
import com.sistemaestudos.model.City
import com.sistemaestudos.model.Weather
import com.sistemaestudos.ui.nav.Route
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import com.sistemaestudos.R

@Composable
fun ListPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val activity = LocalContext.current as? Activity
    val cityList = viewModel.cities

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(8.dp)
    ) {
        items(items = cityList, key = { it.name }) { city ->
            CityItem(
                city = city,
                weather = viewModel.weather(city.name),
                onClick = {
                    viewModel.city = city.name
                    viewModel.page = Route.Home
                },
                onClose = {
                    viewModel.remove(city)
                }
            )
        }
    }
}
@Composable
fun CityItem(
    city: City,
    weather: Weather,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val desc = if (weather == Weather.LOADING) "Carregando clima..." else weather.desc

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = weather.imgUrl,
            modifier = modifier.size(75.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem"
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                modifier = Modifier,
                text = city.name,
                fontSize = 24.sp
            )
            Text(
                modifier = Modifier,
                text = desc,
                fontSize = 16.sp
            )
        }
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
    }
}