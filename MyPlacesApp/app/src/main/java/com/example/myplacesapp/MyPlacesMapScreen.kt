package com.example.myplacesapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.myplacesapp.ui.theme.MyPlacesAppTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.text.DecimalFormat

// Estado da UI para lidar com carregamento e sucesso
sealed interface MapUiState {
    object Loading : MapUiState
    data class Success(val lastKnownLocation: LatLng) : MapUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlacesMapScreen(viewModel: MyPlacesViewModel) {
    val context = LocalContext.current
    var uiState by remember { mutableStateOf<MapUiState>(MapUiState.Loading) }

    // Estados para o diálogo de adicionar local
    var showAddPlaceDialog by remember { mutableStateOf(false) }
    var newPlaceLatLng by remember { mutableStateOf<LatLng?>(null) }

    // Lógica de permissão e obtenção de localização
    RequestLocationPermission { isGranted ->
        if (isGranted) {
            getCurrentLocation(context) { latLng ->
                uiState = MapUiState.Success(latLng)
            }
        } else {
            // Se permissão for negada, usa uma localização padrão (Quixadá, CE)
            uiState = MapUiState.Success(LatLng(-4.9708, -39.0153))
        }
    }

    // Aplicando nosso tema personalizado
    MyPlacesAppTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("MyPlaces App", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (val state = uiState) {
                    is MapUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is MapUiState.Success -> {
                        MyGoogleMap(
                            viewModel = viewModel,
                            initialLocation = state.lastKnownLocation,
                            onMapLongClick = { latLng ->
                                newPlaceLatLng = latLng
                                showAddPlaceDialog = true
                            }
                        )
                    }
                }

                // Card que aparece quando 2 locais são selecionados
                AnimatedVisibility(
                    visible = viewModel.selectedPlaces.size == 2,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    SelectionInfoCard(
                        place1 = viewModel.selectedPlaces[0],
                        place2 = viewModel.selectedPlaces[1],
                        onClear = { viewModel.clearSelection() }
                    )
                }
            }
        }
    }

    // Diálogo para adicionar um novo local
    if (showAddPlaceDialog && newPlaceLatLng != null) {
        AddPlaceDialog(
            onDismissRequest = { showAddPlaceDialog = false },
            onConfirm = { placeName ->
                viewModel.addPlace(MyPlace(placeName, newPlaceLatLng!!))
                showAddPlaceDialog = false
                newPlaceLatLng = null
            }
        )
    }
}

@Composable
private fun MyGoogleMap(
    viewModel: MyPlacesViewModel,
    initialLocation: LatLng,
    onMapLongClick: (LatLng) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f)
    }


    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true,
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true,
            zoomControlsEnabled = false
        ),
        onMapLongClick = onMapLongClick
    ) {
        // Marcadores adicionados pelo usuário
        viewModel.myPlaces.forEach { place ->
            val isSelected = viewModel.selectedPlaces.contains(place)
            val iconColor = if (isSelected) BitmapDescriptorFactory.HUE_AZURE else BitmapDescriptorFactory.HUE_RED

            Marker(
                state = MarkerState(position = place.latLng),
                title = place.name,
                snippet = "Clique para selecionar",
                icon = BitmapDescriptorFactory.defaultMarker(iconColor),
                onClick = {
                    viewModel.toggleSelected(place)
                    true
                }
            )
        }

        // Desenha a polilinha se dois lugares estiverem selecionados
        if (viewModel.selectedPlaces.size == 2) {
            Polyline(
                points = listOf(
                    viewModel.selectedPlaces[0].latLng,
                    viewModel.selectedPlaces[1].latLng
                ),
                color = MaterialTheme.colorScheme.secondary,
                width = 12f
            )
        }
    }
}

@Composable
fun AddPlaceDialog(onDismissRequest: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Adicionar Novo Local") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Nome do local") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = { if (text.isNotBlank()) onConfirm(text) },
                enabled = text.isNotBlank()
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun SelectionInfoCard(place1: MyPlace, place2: MyPlace, onClear: () -> Unit) {
    val distance = calculateDistance(place1.latLng, place2.latLng)
    val df = DecimalFormat("#.##")

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seleção", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                IconButton(onClick = onClear, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Limpar seleção")
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("De: ${place1.name}", fontSize = 14.sp)
            Text("Para: ${place2.name}", fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Distância: ${df.format(distance / 1000)} km",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun RequestLocationPermission(onResult: (Boolean) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onResult
    )

    LaunchedEffect(Unit) {
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> onResult(true)
            else -> launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

private fun getCurrentLocation(context: Context, onLocation: (LatLng) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
            loc?.let {
                onLocation(LatLng(it.latitude, it.longitude))
            }
        }
    } catch (e: SecurityException) {
        // Tratar exceção de segurança
    }
}

private fun calculateDistance(start: LatLng, end: LatLng): Float {
    val results = FloatArray(1)
    Location.distanceBetween(start.latitude, start.longitude, end.latitude, end.longitude, results)
    return results[0]
}