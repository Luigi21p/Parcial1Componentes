package com.ud.parcial1componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Reserva(
    val nombre: String,
    val apellido: String,
    val fecha: String,
    val hora: String,
    val pista: String,
    val estado: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoReservasScreen() {

    var busqueda by remember { mutableStateOf("") }

    val reservas = listOf(
        Reserva("Carlos", "García", "12/05/2026", "4:00 PM", "2", "Activa"),
        Reserva("Ana", "López", "12/05/2026", "5:00 PM", "1", "Activa"),
        Reserva("Luis", "Pérez", "12/05/2026", "6:00 PM", "4", "Finalizada")
    )

    val reservasFiltradas = reservas.filter {
        "${it.nombre} ${it.apellido}".contains(busqueda, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Listado de Reservas") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("Buscar reserva...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1565C0))
                    .padding(vertical = 6.dp, horizontal = 4.dp)
            ) {
                Text("Cliente", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f), fontSize = 12.sp)
                Text("Fecha", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f), fontSize = 12.sp)
                Text("Hora", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f), fontSize = 12.sp)
                Text("Pista", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f), fontSize = 12.sp)
                Text("Estado", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f), fontSize = 12.sp)
                Text("Acc.", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f), fontSize = 12.sp)
            }

            LazyColumn {
                items(reservasFiltradas) { reserva ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${reserva.nombre} ${reserva.apellido}", modifier = Modifier.weight(1.5f), fontSize = 12.sp)
                        Text(reserva.fecha, modifier = Modifier.weight(1.5f), fontSize = 12.sp)
                        Text(reserva.hora, modifier = Modifier.weight(1.2f), fontSize = 12.sp)
                        Text(reserva.pista, modifier = Modifier.weight(0.8f), fontSize = 12.sp)

                        Box(modifier = Modifier.weight(1.3f)) {
                            Text(
                                text = reserva.estado,
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .background(
                                        if (reserva.estado == "Activa") Color(0xFF4CAF50)
                                        else Color(0xFF9E9E9E),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Box(modifier = Modifier.weight(0.8f)) {
                            TextButton(
                                onClick = {},
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("✏️", fontSize = 16.sp)
                            }
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}