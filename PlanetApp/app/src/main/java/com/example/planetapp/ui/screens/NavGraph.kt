package com.example.planetapp.ui.screens

import BottomNavigationBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.planetapp.models.planetList

sealed class BottomBarScreen(val route: String, val icon:
@Composable () -> Unit, val label: String) {
    object Home : BottomBarScreen(
        route = "home",
        icon = { androidx.compose.material.icons.Icons.Default.Home },
        label = "Home"
    )
    object Favorite : BottomBarScreen(
        route = "favorite",
        icon = { androidx.compose.material.icons.Icons.Default.Favorite },
        label = "Favoritos"
    )
}

@ExperimentalMaterial3Api
@Composable
fun NavGraph(
    onSettingClick: () -> Unit,
    onHelpClick: () -> Unit,
){
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) {
            innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomBarScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomBarScreen.Home.route){
                HomeScreen(
                    onPlanetSelected = {planet ->
                        navController.navigate("detalis/${planet.name}")
                    },
                    onSettingClick = onSettingClick,
                    onHelpClick = onHelpClick
                )
            }
            composable(BottomBarScreen.Favorite.route){
                FavoriteScreen(
                    onPlanetSelected = { planet ->
                        navController.navigate("detail/${planet.name}")
                    },
                    onFavoriteToggle = {planet ->
                        planet.isFavorite = !planet.isFavorite
                    }
                )
            }
            composable("details/{planetName}"){
                    backStackEntry ->
                val planetName = backStackEntry.arguments?.getString("planetName")
                val selectedPlanet = planetList.first { it.name == planetName }
                DetailsScreen(planet = selectedPlanet)
            }
        }
    }
}