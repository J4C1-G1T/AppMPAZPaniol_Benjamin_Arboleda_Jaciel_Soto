package com.proyecto.appmpazpaniol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.proyecto.appmpazpaniol.ui.navigation.AppNavigation
import com.proyecto.appmpazpaniol.ui.theme.AppMPAZPaniolTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppMPAZPaniolTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llama al grafo de navegacion unificado
                    AppNavigation()
                }
            }
        }
    }
}