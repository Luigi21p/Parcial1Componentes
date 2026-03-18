package com.ud.parcial1componentes.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ud.parcial1componentes.logica.Reserva
import com.ud.parcial1componentes.persistencia.DatabaseHelper
import com.ud.parcial1componentes.persistencia.ReservaDAO

// Colores del tema
private val AzulOscuro = Color(0xFF0D1B2A)
private val AzulMedio = Color(0xFF1B3A5C)
private val AzulAcento = Color(0xFF2196F3)
private val Verde = Color(0xFF4CAF50)
private val Rojo = Color(0xFFE53935)
private val Naranja = Color(0xFFFF9800)
private val BlancoSuave = Color(0xFFF0F4F8)
private val GrisTarjeta = Color(0xFF1E2D3D)

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
            title = {
                Text(
                    "Confirmar eliminación",
                    color = BlancoSuave,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "¿Estás seguro de eliminar la reserva de ${reservaAEliminar!!.clienteCompleto}?",
                    color = BlancoSuave.copy(alpha = 0.8f)
                )
            },
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
                    Text("Eliminar", color = Rojo)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar", color = AzulAcento)
                }
            },
            containerColor = GrisTarjeta,
            titleContentColor = BlancoSuave,
            textContentColor = BlancoSuave
        )
    }


    // ESTRUCTURA PRINCIPAL DE LA PANTALLA
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Listado de Reservas",
                        fontWeight = FontWeight.Bold,
                        color = BlancoSuave
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulOscuro
                )
            )
        },
        floatingActionButton = {
            // Botón flotante para crear nueva reserva
            FloatingActionButton(
                onClick = { navController.navigate("crear") },
                containerColor = AzulAcento,
                contentColor = BlancoSuave
            ) {
                Text("+", fontSize = 24.sp)  // Símbolo de agregar
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AzulOscuro)
                .padding(padding)
                .padding(12.dp)
        ) {

            // BARRA DE BÚSQUEDA
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },  // Cada letra actualiza la búsqueda
                placeholder = {
                    Text(
                        "Buscar reserva por nombre del cliente...",
                        color = BlancoSuave.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AzulAcento,
                    unfocusedBorderColor = AzulMedio,
                    focusedTextColor = BlancoSuave,
                    unfocusedTextColor = BlancoSuave,
                    cursorColor = AzulAcento,
                    focusedPlaceholderColor = BlancoSuave.copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = BlancoSuave.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Reservas encontradas: ${reservas.size}",
                color = BlancoSuave.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // LISTA DE RESERVAS en formato vertical
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(reservas) { reserva ->
                    TarjetaReservaCompacta(
                        reserva = reserva,
                        onEditar = {
                            navController.navigate("editar/${reserva.id}")
                        },
                        onEliminar = {
                            reservaAEliminar = reserva
                            mostrarDialogoEliminar = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaReservaCompacta(
    reserva: Reserva,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val colorEstado = when (reserva.estadoId) {
        1 -> Verde
        2 -> Rojo
        else -> Naranja
    }

    // Formatear fecha
    val fechaFormateada = try {
        val partes = reserva.fecha.split("-")
        "${partes[2]}/${partes[1]}/${partes[0]}"
    } catch (e: Exception) {
        reserva.fecha
    }

    // Formatear hora
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

    val numeroPista = reserva.pistaNombre.replace("Pista ", "")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GrisTarjeta)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Información principal
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nombre del cliente
                Text(
                    text = reserva.clienteCompleto,
                    color = BlancoSuave,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )

                // Fecha, hora y pista en una línea
                Text(
                    text = "$fechaFormateada · $horaFormateada · Pista $numeroPista",
                    color = BlancoSuave.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }

            // Estado (como un punto de color)
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(colorEstado)
                    .padding(end = 4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Botones de acción (solo iconos)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Botón Editar
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AzulMedio),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = onEditar,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("✏️", fontSize = 14.sp)
                    }
                }

                // Botón Eliminar
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AzulMedio),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = onEliminar,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("🗑️", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}