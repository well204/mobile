package com.example.myplacesapp

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

/**
 * Modelo de dados para um local.
 * Adicionado um ID para garantir que cada lugar seja único.
 */


class MyPlacesViewModel : ViewModel() {

    // 1. Tornamos a lista mutável privada para proteger o estado.
    private val _myPlaces = mutableStateListOf<MyPlace>()
    // A UI observa esta lista pública e imutável.
    val myPlaces: List<MyPlace> = _myPlaces

    // O mesmo padrão para os locais selecionados.
    private val _selectedPlaces = mutableStateListOf<MyPlace>()
    val selectedPlaces: List<MyPlace> = _selectedPlaces

    fun addPlace(place: MyPlace) {
        _myPlaces.add(place)
    }

    fun toggleSelected(place: MyPlace) {
        if (_selectedPlaces.contains(place)) {
            _selectedPlaces.remove(place)
        } else {
            // Sua lógica original foi mantida: se já houver 2 selecionados,
            // a seleção é limpa e o novo lugar é adicionado.
            if (_selectedPlaces.size >= 2) {
                _selectedPlaces.clear()
            }
            _selectedPlaces.add(place)
        }
    }

    /**
     * 2. Nova função para limpar a seleção.
     * Necessária para o botão 'X' no card de informações.
     */
    fun clearSelection() {
        _selectedPlaces.clear()
    }
}