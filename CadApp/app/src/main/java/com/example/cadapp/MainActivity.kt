package com.example.cadapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cadapp.ui.theme.CadAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CadAppTheme {
                CadApp()
            }
        }
    }
}

@Composable
fun CadApp() {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDay by remember { mutableStateOf("") }
    var cep by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var serie by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var music by remember { mutableStateOf("") }
    var film by remember { mutableStateOf("") }
    var team by remember { mutableStateOf("") }
    var mail by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }



    LazyColumn  (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        item{
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome:") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Sobrenome:") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = birthDay,
                onValueChange = { birthDay = it },
                label = { Text("Data de nascimento:") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = cep,
                onValueChange = { cep = it },
                label = { Text("Cep:") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = nationality,
                onValueChange = { nationality = it },
                label = { Text("Nacionalidade:") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = state,
                onValueChange = { state = it },
                label = { Text("Estado:") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text("Gênero:") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = neighborhood,
                onValueChange = { neighborhood = it },
                label = { Text("Bairro:") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = mail,
                onValueChange = { mail = it },
                label = { Text("Email:") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Cidade:") },
                modifier = Modifier.fillMaxWidth()

            )

            TextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Celular:") },
                modifier = Modifier.fillMaxWidth()

            )

            TextField(
                value = team,
                onValueChange = { team = it },
                label = { Text("Time:") },
                modifier = Modifier.fillMaxWidth()

            )

            TextField(
                value = music,
                onValueChange = { music = it },
                label = { Text("Música favorita:") },
                modifier = Modifier.fillMaxWidth()

            )

            TextField(
                value = film,
                onValueChange = { film = it },
                label = { Text("Filme favorito:") },
                modifier = Modifier.fillMaxWidth()

            )

            TextField(
                value = serie,
                onValueChange = { serie = it },
                label = { Text("Serie favorita:") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row (
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                    },
                    modifier = Modifier.weight(1f).padding(16.dp)
                ) {
                    Text(text = "Enviar")
                }

                Button(
                    onClick = {
                        name = ""
                        lastName = ""
                        birthDay = ""
                        cep = ""
                        nationality = ""
                        state = ""
                        gender = ""
                        neighborhood = ""
                        mail = ""
                        city = ""
                        phone = ""
                        team = ""
                        music = ""
                        film = ""
                        serie = ""
                    },
                    modifier = Modifier.weight(1f).padding(16.dp)
                ) {
                    Text(text = "Limpar")
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CadAppTheme {
        CadApp()
    }
}