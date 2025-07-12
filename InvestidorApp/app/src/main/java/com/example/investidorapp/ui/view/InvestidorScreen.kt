package com.example.investidorapp.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MonetizationOn // <<< MUDANÇA: Ícone mais relacionado a finanças
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.investidorapp.model.Investimento
import com.example.investidorapp.viewmodel.InvestimentosViewModel

// --- Paleta de Cores Moderna ---
// <<< MUDANÇA: Definindo nossas cores customizadas para fácil reutilização
val DarkCharcoal = Color(0xFF1E2124)
val SlateGray = Color(0xFF282B30)
val DeepTeal = Color(0xFF00BFA5)
val OffWhite = Color(0xFFF5F5F5)
val SoftRed = Color(0xFFE57373)
// --- Fim da Paleta ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestidorScreen(viewModel: InvestimentosViewModel) {
    val investimentos by viewModel.investimentos.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var investimentoParaRemover by remember { mutableStateOf<Investimento?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SmallTopAppBar(
                title = { Text("Meus Investimentos", style = MaterialTheme.typography.titleLarge) }, // <<< MUDANÇA: Título mais descritivo
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = DeepTeal, // <<< MUDANÇA
                    titleContentColor = Color.White // <<< MUDANÇA
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = DeepTeal, // <<< MUDANÇA
                contentColor = Color.White // <<< MUDANÇA
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Investimento")
            }
        },
        containerColor = DarkCharcoal // <<< MUDANÇA: Cor de fundo principal do Scaffold
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ListaInvestimentos(
                investimentos = investimentos,
                onRemoveClick = { investimento ->
                    investimentoParaRemover = investimento
                    showDeleteDialog = true
                }
            )

            if (showAddDialog) {
                // O diálogo de Adicionar usará as cores do tema padrão, o que cria um bom contraste.
                // Se quisesse estilizá-lo, faria aqui.
                AddInvestimentoDialog(
                    onDismiss = { showAddDialog = false },
                    onConfirm = { nome, valor ->
                        viewModel.addInvestimento(nome, valor)
                        showAddDialog = false
                    }
                )
            }

            if (showDeleteDialog && investimentoParaRemover != null) {
                DeleteConfirmationDialog(
                    onConfirm = {
                        investimentoParaRemover?.let { viewModel.removerInvestimento(it) }
                        showDeleteDialog = false
                        investimentoParaRemover = null
                    },
                    onDismiss = {
                        showDeleteDialog = false
                        investimentoParaRemover = null
                    }
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Remoção") },
        text = { Text("Você tem certeza que deseja remover este investimento?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = SoftRed) // <<< MUDANÇA
            ) {
                Text("Remover", color = Color.White) // <<< MUDANÇA
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = DeepTeal) // <<< MUDANÇA
            }
        }
    )
}

@Composable
fun AddInvestimentoDialog(onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit) {
    // Código existente do AddInvestimentoDialog mantido, pois não estava no prompt.
    // Ele funcionará normalmente.
    var nome by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Investimento") },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do Ativo") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = valor,
                    onValueChange = { valor = it.filter { char -> char.isDigit() } },
                    label = { Text("Valor (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val valorInt = valor.toIntOrNull() ?: 0
                if (nome.isNotBlank() && valorInt > 0) {
                    onConfirm(nome, valorInt)
                }
            }) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun ListaInvestimentos(
    investimentos: List<Investimento>,
    onRemoveClick: (Investimento) -> Unit
) {
    if (investimentos.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Nenhum investimento adicionado",
                style = MaterialTheme.typography.bodyLarge,
                color = OffWhite.copy(alpha = 0.7f) // <<< MUDANÇA
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(), // <<< MUDANÇA: Removido padding para ir até as bordas
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp) // <<< MUDANÇA: Padding interno
        ) {
            items(investimentos, key = { it.key }) { investimento ->
                InvestimentoItem(
                    investimento = investimento,
                    onRemoveClick = { onRemoveClick(investimento) }
                )
            }
        }
    }
}

@Composable
fun InvestimentoItem(
    investimento: Investimento,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = SlateGray), // <<< MUDANÇA
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MonetizationOn, // <<< MUDANÇA: Ícone novo
                contentDescription = "Ícone Investimento",
                tint = DeepTeal, // <<< MUDANÇA
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = investimento.nome,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OffWhite // <<< MUDANÇA
                )
                Text(
                    text = "R$${investimento.valor}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp), // <<< MUDANÇA: Tamanho um pouco maior
                    color = OffWhite.copy(alpha = 0.8f) // <<< MUDANÇA
                )
            }
            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover Investimento",
                    tint = SoftRed // <<< MUDANÇA
                )
            }
        }
    }
}