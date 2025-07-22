package com.example.myplacesapp

import com.google.android.gms.maps.model.LatLng

data class MyPlace(
    val name: String,
    val latLng: LatLng,
    val id: Long = System.nanoTime() // Usar nanoTime para um ID único mais robusto
)
