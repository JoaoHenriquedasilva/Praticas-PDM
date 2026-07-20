package com.sistemaestudos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sistemaestudos.api.WeatherService
import com.sistemaestudos.db.fb.FBDatabase
import com.sistemaestudos.ui.CityDialog
import com.sistemaestudos.ui.nav.BottomNavBar
import com.sistemaestudos.ui.nav.BottomNavItem
import com.sistemaestudos.ui.nav.MainNavHost
import com.sistemaestudos.ui.nav.Route
import com.sistemaestudos.ui.theme.SistemaestudosTheme
import com.sistemaestudos.monitor.ForecastMonitor
import android.content.Intent
import androidx.compose.runtime.DisposableEffect
import androidx.core.util.Consumer
import com.sistemaestudos.db.local.LocalDatabase
import com.sistemaestudos.repo.Repository

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val monitor = ForecastMonitor(this)

        setContent {
            val fbDB = remember { FBDatabase() }
            val localDB = remember { LocalDatabase(context = this, databaseName = "cidades_db") }
            val repository = remember { Repository(fbDB, localDB) }
            val weatherService = remember { WeatherService(this) }
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(repository, weatherService, monitor)
            )

            DisposableEffect(Unit) {
                val listener = Consumer<Intent> { intent ->
                    viewModel.city = intent.getStringExtra("city")
                    viewModel.page = Route.Home
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }

            val navController = rememberNavController()
            var showDialog by remember { mutableStateOf(false) }
            val currentRoute = navController.currentBackStackEntryAsState()
            val showButton = currentRoute.value?.destination?.hasRoute(Route.List::class) == true
            val fbUser by viewModel.user.collectAsStateWithLifecycle()

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted -> }
            )

            if (showDialog) CityDialog(
                onDismiss = { showDialog = false },
                onConfirm = { city ->
                    if (city.isNotBlank()) viewModel.addCity(city)
                    showDialog = false
                })

            SistemaestudosTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                val name = fbUser?.name ?: "[carregando...]"
                                Text("Bem-vindo/a! $name")
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Sair"
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        val items = listOf(
                            BottomNavItem.HomeButton,
                            BottomNavItem.ListButton,
                            BottomNavItem.MapButton,
                        )
                        BottomNavBar(
                            viewModel = viewModel,
                            navController = navController,
                            items = items
                        )
                    },
                    floatingActionButton = {
                        if (showButton) {
                            FloatingActionButton(onClick = { showDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Adicionar")
                            }
                        }
                    }
                ) { innerPadding ->
                    launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)

                    Box(modifier = Modifier.padding(innerPadding)) {
                        MainNavHost(
                            navController = navController,
                            viewModel = viewModel
                        )

                        LaunchedEffect(viewModel.page) {
                            navController.navigate(viewModel.page) {
                                navController.graph.startDestinationRoute?.let {
                                    popUpTo(it) {
                                        saveState = true
                                    }
                                }
                                restoreState = true
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        }
    }
}