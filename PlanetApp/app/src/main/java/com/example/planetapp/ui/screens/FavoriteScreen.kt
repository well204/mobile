package com.example.planetapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.planetapp.models.Planet
import com.example.planetapp.models.planetList
import com.example.planetapp.ui.components.PlanetListItem

@ExperimentalMaterial3Api
@Composable
fun FavoriteScreen(
    onPlanetSelected: (Planet) -> Unit,
    onFavoriteToggle: (Planet) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Favoritos",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { innerPadding ->
        val favoritePlanets = planetList.filter { it.isFavorite }
        if (favoritePlanets.isEmpty()){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center

            ){
                Text(
                    text = "Você ainda não adicionou nenhum planeta aos favoritos.",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else{

            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 8.dp)
            ) {
                items(favoritePlanets){ planet ->
                    PlanetListItem(
                        planet = planet,
                        onPlanetSelected = { onPlanetSelected(it) },
                        onFavoriteToggle = { onFavoriteToggle(it) }

                    )
                }

            }
        }

    }
}