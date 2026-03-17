package com.ud.parcial1componentes


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(navController: NavController) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(
            onClick = { navController.navigate("crear") },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Crear Reserva")
        }

        Button(
            onClick = { navController.navigate("lista") },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Ver Reservas")
        }

    }
}