package com.example.cruditemapp1.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cruditemapp1.model.Item
import com.example.cruditemapp1.viewmodel.ItemViewModel

// --- Paleta de Cores Sugerida ---
// Em um app real, coloque isso no seu arquivo ui/theme/Color.kt
val DarkBlue = Color(0xFF0D47A1)
val LightBlue = Color(0xFF42A5F5)
val WhiteCream = Color(0xFFFAFAFA)
val CardBackground = Color.White
val DestructiveRed = Color(0xFFD32F2F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(
    modifier: Modifier = Modifier,
    viewModel: ItemViewModel = viewModel()
) {
    val items by viewModel.items
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Itens Cadastrados") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = WhiteCream
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Seção de Adicionar Item
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título do Item") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrição") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (title.text.isNotEmpty() && description.text.isNotEmpty()) {
                                viewModel.addItem(Item(title = title.text, description = description.text))
                                title = TextFieldValue("")
                                description = TextFieldValue("")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = LightBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar Item")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ADICIONAR NOVO ITEM")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de Itens
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = items,
                    key = { item -> item.id } // Chave para performance
                ) { item ->
                    ItemCard(
                        item = item,
                        onUpdateClick = {
                            selectedItem = item
                            showDialog = true
                        },
                        onDeleteClick = {
                            viewModel.deleteItem(item.id)
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        UpdateItemDialog(
            item = selectedItem,
            onDismiss = { showDialog = false },
            onUpdate = { updatedItem ->
                viewModel.updateItem(updatedItem)
                showDialog = false
            }
        )
    }
}

@Composable
fun ItemCard(
    item: Item,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Botões de Ação
            Row {
                IconButton(
                    onClick = onUpdateClick,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = DarkBlue)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar Item")
                }
                IconButton(
                    onClick = onDeleteClick,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = DestructiveRed)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Deletar Item")
                }
            }
        }
    }
}


@Composable
fun UpdateItemDialog(
    item: Item?,
    onDismiss: () -> Unit,
    onUpdate: (Item) -> Unit
) {
    if (item == null) return

    var title by remember(item) { mutableStateOf(TextFieldValue(item.title)) }
    var description by remember(item) { mutableStateOf(TextFieldValue(item.description)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onUpdate(item.copy(title = title.text, description = description.text))
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Gray)
            ) {
                Text("Cancelar")
            }
        }
    )
}