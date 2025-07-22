package com.example.myplacesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = MyPlacesViewModel()
        setContent {
            MyPlacesMapScreen(viewModel)
        }
    }
}

