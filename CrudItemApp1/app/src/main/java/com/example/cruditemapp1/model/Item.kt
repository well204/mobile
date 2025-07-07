package com.example.cruditemapp1.model

data class Item (
    // Corrigido para "generatedId"
    val id: String = generatedId().toString(),
    val title: String = "",
    val description: String = ""
) {

    companion object {
        private var currentId = 0

        // Corrigido para "generatedId"
        fun generatedId(): Int {
            currentId++
            return currentId
        }
    }
}