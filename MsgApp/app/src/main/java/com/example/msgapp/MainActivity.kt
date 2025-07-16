package com.example.msgapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.msgapp.ui.view.ChatScreen
import com.example.msgapp.ui.view.RoomSelector
import com.example.msgapp.ui.view.notifyNewMessage
import com.example.msgapp.viewmodel.MsgViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            MsgAppTheme {
                MsgAppRoot()
            }
        }
    }
}

@Composable
fun MsgAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = lightColors(
            primary = androidx.compose.ui.graphics.Color(0xFF1976D2),
            secondary = androidx.compose.ui.graphics.Color(0xFF42A5F5)
        ),
        content = content
    )
}

@Composable
fun MsgAppRoot(vm: MsgViewModel = viewModel()) {
    val context = LocalContext.current

    // Login anônimo do Firebase
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val user by produceState(initialValue = firebaseAuth.currentUser) {
        if (value == null) {
            firebaseAuth.signInAnonymously()
                .addOnCompleteListener { task -> value = firebaseAuth.currentUser }
        }
    }
    val userId = user?.uid ?: "pedro"
    var userName by remember { mutableStateOf("Usuário-${userId.takeLast(4)}") }
    var currentRoom by remember { mutableStateOf("geral") }
    var lastNotifiedId by remember { mutableStateOf<String?>(null) }

    // Controle da permissão de notificação
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else {
            mutableStateOf(true)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )

    // Solicita a permissão ao iniciar, se necessário
    LaunchedEffect(key1 = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(currentRoom) {
        vm.switchRoom(currentRoom)
    }

    Column {
        RoomSelector(onRoomSelected = { if (it.isNotBlank()) currentRoom = it })
        ChatScreen(
            username = userName,
            userId = userId,
            messages = vm.messages.collectAsState().value,
            onSend = { text -> vm.sendMessage(userId, userName, text) },
            currentRoom = currentRoom,
            lastNotifiedId = lastNotifiedId,
            onNotify = { msg ->
                if (hasNotificationPermission) {
                    notifyNewMessage(context, msg)
                    lastNotifiedId = msg.id
                }
            }
        )
    }
}