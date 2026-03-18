package com.ud.parcial1componentes.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ud.parcial1componentes.logica.Reserva
import com.ud.parcial1componentes.persistencia.DatabaseHelper
import com.ud.parcial1componentes.persistencia.ReservaDAO

// muestra todas las reservas en una tabla
// Permite buscar, editar y eliminar reservas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoReservasScreen(navController: NavController) {

    // conexión a la base de datos)
    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context)
    val reservaDAO = ReservaDAO(dbHelper)

    // Lista de reservas que se muestra en pantalla
    var reservas by remember { mutableStateOf(listOf<Reserva>()) }

    // Texto de búsqueda
    var busqueda by remember { mutableStateOf("") }

    // Control del diálogo de eliminación
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var reservaAEliminar by remember { mutableStateOf<Reserva?>(null) }

    // se ejecuta cuando cambia la búsqueda

    // LaunchedEffect = "Ejecutar esto cada vez que cambie 'busqueda'"
    LaunchedEffect(busqueda) {
        reservas = if (busqueda.isBlank()) {
            // Si no hay búsqueda, traer TODAS las reservas
            reservaDAO.obtenerTodasLasReservas()
        } else {
            // Si hay búsqueda, filtrar por nombre
            reservaDAO.buscarReservasPorCliente(busqueda)
        }
    }

    // RECARGAR después de eliminar
    fun recargarReservas() {
        reservas = if (busqueda.isBlank()) {
            reservaDAO.obtenerTodasLasReservas()
        } else {
            reservaDAO.buscarReservasPorCliente(busqueda)
        }
    }

    // DIÁLOGO DE CONFIRMACIÓN PARA ELIMINAR

    if (mostrarDialogoEliminar && reservaAEliminar != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de eliminar la reserva de ${reservaAEliminar!!.clienteCompleto}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        //  Eliminar de la base de datos
                        reservaDAO.eliminarReserva(reservaAEliminar!!.id)

                        // Cerrar el diálogo
                        mostrarDialogoEliminar = false
                        reservaAEliminar = null

                        //  Recargar la lista para que desaparezca
                        recargarReservas()
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }


    // ESTRUCTURA PRINCIPAL DE LA PANTALLA
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Listado de Reservas") })
        },
        floatingActionButton = {
            // Botón flotante para crear nueva reserva
            FloatingActionButton(
                onClick = { navController.navigate("crear") }
            ) {
                Text("+")  // Símbolo de agregar
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {

            // BARRA DE BÚSQUEDA
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },  // Cada letra actualiza la búsqueda
                placeholder = { Text("Buscar reserva...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ENCABEZADO DE LA TABLA (títulos de columnas)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1565C0))  // Azul oscuro
                    .padding(vertical = 6.dp, horizontal = 4.dp)
            ) {
                // Cada columna tiene un "weight" que determina su ancho
                Text("Cliente", color = Color.White, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.5f), fontSize = 12.sp)
                Text("Fecha", color = Color.White, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.5f), fontSize = 12.sp)
                Text("Hora", color = Color.White, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.2f), fontSize = 12.sp)
                Text("Pista", color = Color.White, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.8f), fontSize = 12.sp)
                Text("Estado", color = Color.White, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.3f), fontSize = 12.sp)
                Text("Acc.", color = Color.White, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.8f), fontSize = 12.sp)
            }

            // LISTA DE RESERVAS (una fila por cada reserva)

            LazyColumn {
                items(reservas) { reserva ->
                    // FILA DE RESERVA
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // COLUMNA 1: Cliente (nombre completo)
                        Text(
                            reserva.clienteCompleto,
                            modifier = Modifier.weight(1.5f),
                            fontSize = 12.sp
                        )

                        // COLUMNA 2: Fecha (convertir YYYY-MM-DD a DD/MM/AAAA)
                        val fechaFormateada = try {
                            val partes = reserva.fecha.split("-")
                            "${partes[2]}/${partes[1]}/${partes[0]}"
                        } catch (e: Exception) {
                            reserva.fecha  // Si hay error, mostrar original
                        }
                        Text(fechaFormateada, modifier = Modifier.weight(1.5f), fontSize = 12.sp)

                        // COLUMNA 3: Hora (convertir 24h a 12h AM/PM)
                        val horaFormateada = try {
                            val partes = reserva.hora.split(":")
                            val hora = partes[0].toInt()
                            val minutos = partes[1]
                            val ampm = if (hora < 12) "AM" else "PM"
                            val h12 = if (hora % 12 == 0) 12 else hora % 12
                            "${h12}:${minutos} $ampm"
                        } catch (e: Exception) {
                            reserva.hora
                        }
                        Text(horaFormateada, modifier = Modifier.weight(1.2f), fontSize = 12.sp)

                        // COLUMNA 4: Pista (número)
                        val numeroPista = reserva.pistaNombre.replace("Pista ", "")
                        Text(numeroPista, modifier = Modifier.weight(0.8f), fontSize = 12.sp)

                        // COLUMNA 5: Estado (con color de fondo)
                        Box(modifier = Modifier.weight(1.3f)) {
                            Text(
                                text = reserva.estadoNombre,
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .background(
                                        when (reserva.estadoNombre) {
                                            "Activa" -> Color(0xFF4CAF50)      // Verde
                                            "Cancelada" -> Color(0xFFF44336)   // Rojo
                                            "Completada" -> Color(0xFF9E9E9E)  // Gris
                                            else -> Color(0xFF9E9E9E)
                                        },
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // COLUMNA 6: Botones de acción (Editar y Eliminar)

                        // Botón Editar (✏️)
                        Box(modifier = Modifier.weight(0.8f)) {
                            TextButton(
                                onClick = {
                                    // Navegar a la pantalla de editar con el ID
                                    navController.navigate("editar/${reserva.id}")
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("✏️", fontSize = 16.sp)
                            }
                        }

                        // Botón Eliminar (🗑️)
                        Box(modifier = Modifier.weight(0.8f)) {
                            TextButton(
                                onClick = {
                                    // Guardar la reserva a eliminar y mostrar diálogo
                                    reservaAEliminar = reserva
                                    mostrarDialogoEliminar = true
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("🗑️", fontSize = 16.sp, color = Color.Red)
                            }
                        }
                    }

                    // Línea divisoria entre reservas
                    HorizontalDivider()
                }
            }
        }
    }
}