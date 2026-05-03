package com.sistemaestudos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.sistemaestudos.ui.HomePage
import com.sistemaestudos.ui.theme.SistemaestudosTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SistemaestudosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    HomePage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

