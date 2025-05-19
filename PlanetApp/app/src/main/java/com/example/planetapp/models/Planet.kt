package com.example.planetapp.models

import com.example.planetapp.R

data class Planet(
    val id: Int,
    val name: String,
    val type: String,
    val galaxy: String,
    val distanceFromSun: String,
    val diameter: String,
    val characteristics: String,
    val imageRes: Int,
    var isFavorite: Boolean = false
)

val planetList = listOf(
    Planet(
        id = 1,
        name = "Earth",
        type = "Terrestrial" ,
        galaxy = "Milky Way" ,
        distanceFromSun = "149.6 million km" ,
        diameter = "12,742 km" ,
        characteristics = "Supports life, has water and atmosphere." ,
        imageRes = R.drawable .terra
    ),

    Planet(
        id = 2,
        name = "Jupter",
        type = "Terrestrial" ,
        galaxy = "Milky Way" ,
        distanceFromSun = "276.9 million km" ,
        diameter = "30,352 km" ,
        characteristics = "Dont has life." ,
        imageRes = R.drawable .jupiter
    ),

    Planet(
        id = 3,
        name = "Mercurio",
        type = "Terrestrial" ,
        galaxy = "Milky Way" ,
        distanceFromSun = "19.6 million km" ,
        diameter = "58884,72 km" ,
        characteristics = "Dont suports life" ,
        imageRes = R.drawable .mercurio
    ),
)