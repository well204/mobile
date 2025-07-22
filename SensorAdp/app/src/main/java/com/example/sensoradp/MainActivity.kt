package com.example.sensoradp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
// O nome do seu tema pode ser diferente. Verifique o seu arquivo ui/theme/Theme.kt
import com.example.sensoradp.ui.theme.SensorAdpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // É uma boa prática envolver sua tela com o tema do app
            SensorAdpTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SensorScreen()
                }
            }
        }
    }
}