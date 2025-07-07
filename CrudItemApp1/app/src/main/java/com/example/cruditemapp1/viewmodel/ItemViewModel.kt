package com.example.cruditemapp1.viewmodel



import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cruditemapp1.model.Item
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ItemViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration ? = null

    var items = mutableStateOf<List<Item>>(listOf())
        private set

    init {
        listenerToItems()
    }

    private fun listenerToItems(){
        listenerRegistration = db.collection("items")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null){
                    return@addSnapshotListener
                }
                if (snapshot != null){
                    val fetchedItems = snapshot.documents.mapNotNull { document ->
                        document.toObject(Item::class.java)?.copy(id = document.id)
                    }
                    items.value = fetchedItems
                }

            }
    }
    fun addItem(item: Item){
        db.collection("items").add(item)
    }
    fun deleteItem(itemId:String){
        db.collection("items").document(itemId).delete()
    }
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    fun updateItem(item: Item){
        db.collection("items").document(item.id).set(item).addOnSuccessListener {
            Log.d("viewmodel","Item atualizado com sucesso")
        }
            .addOnFailureListener {
                Log.d("viewmodel","Erro ao atualizar item")
            }
    }

}


