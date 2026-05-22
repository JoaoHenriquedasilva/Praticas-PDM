package com.sistemaestudos.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sistemaestudos.MainViewModel
import com.sistemaestudos.ui.HomePage
import com.sistemaestudos.ui.ListPage
import com.sistemaestudos.ui.MapPage

@Composable
fun MainNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(modifier = modifier) }
        composable<Route.List> {ListPage(viewModel = viewModel, modifier = modifier) }
        composable<Route.Map> {MapPage(viewModel = viewModel) }
    }
}