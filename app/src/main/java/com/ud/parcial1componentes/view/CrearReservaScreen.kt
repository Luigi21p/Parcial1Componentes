package com.ud.parcial1componentes.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ud.parcial1componentes.persistencia.DatabaseHelper
import com.ud.parcial1componentes.persistencia.ReservaDAO
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Color
private val AzulMedio = Color(0xFF1B3A5C)

// muestra un formulario para crear una nueva reserva
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearReservaScreen(navController: NavController) {

    // conexión a la base de datos
    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context)
    val reservaDAO = ReservaDAO(dbHelper)

    // guardan lo que el usuario escribe
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var pista by remember { mutableStateOf("1") }

    // Control de menús desplegables
    var expandedPista by remember { mutableStateOf(false) }

    // Control calendario y reloj
    var mostrarCalendario by remember { mutableStateOf(false) }
    var mostrarReloj by remember { mutableStateOf(false) }

    // Control de mensajes
    var mostrarMensaje by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var esExito by remember { mutableStateOf(false) }

    // Estados para los selectores
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    // DIÁLOGO DEL CALENDARIO
    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { fechaSeleccionada ->

                            // Obtener la fecha en UTC del DatePicker
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            val fechaUTC = sdf.format(Date(fechaSeleccionada))

                            // Convertir a zona local del dispositivo
                            val sdfLocal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            sdfLocal.timeZone = TimeZone.getDefault()
                            val fechaLocal = sdfLocal.parse(fechaUTC)

                            // Usar Calendar con la fecha local
                            val cal = Calendar.getInstance()
                            cal.time = fechaLocal

                            // Obtener fecha actual (local)
                            val calHoy = Calendar.getInstance()
                            calHoy.set(Calendar.HOUR_OF_DAY, 0)
                            calHoy.set(Calendar.MINUTE, 0)
                            calHoy.set(Calendar.SECOND, 0)
                            calHoy.set(Calendar.MILLISECOND, 0)

                            // Comparar fechas locales
                            if (cal.timeInMillis >= calHoy.timeInMillis) {
                                // Formato: DD/MM/AAAA
                                fecha = String.format("%02d/%02d/%d",
                                    cal.get(Calendar.DAY_OF_MONTH),
                                    cal.get(Calendar.MONTH) + 1,
                                    cal.get(Calendar.YEAR)
                                )
                            } else {
                                mensaje = "No se pueden seleccionar fechas anteriores a hoy"
                                esExito = false
                                mostrarMensaje = true
                            }
                        }
                        mostrarCalendario = false
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text("OK", color = AzulMedio)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text("Cancelar", color = AzulMedio)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // DIÁLOGO DEL RELOJ
    if (mostrarReloj) {
        AlertDialog(
            onDismissRequest = { mostrarReloj = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val h = timePickerState.hour
                        val m = timePickerState.minute

                        // Validar hora actual (solo si es el mismo día)
                        if (fecha.isNotBlank()) {
                            val ahora = Calendar.getInstance()
                            val añoActual = ahora.get(Calendar.YEAR)
                            val mesActual = ahora.get(Calendar.MONTH) + 1
                            val diaActual = ahora.get(Calendar.DAY_OF_MONTH)
                            val horaActual = ahora.get(Calendar.HOUR_OF_DAY)
                            val minutoActual = ahora.get(Calendar.MINUTE)

                            val fechaParts = fecha.split("/")
                            val diaSel = fechaParts[0].toInt()
                            val mesSel = fechaParts[1].toInt()
                            val añoSel = fechaParts[2].toInt()

                            // Si es el mismo día, validar que la hora no sea pasada
                            if (añoSel == añoActual && mesSel == mesActual && diaSel == diaActual) {
                                if (h < horaActual || (h == horaActual && m < minutoActual)) {
                                    mensaje = "No se pueden seleccionar horas anteriores a la actual"
                                    esExito = false
                                    mostrarMensaje = true
                                    mostrarReloj = false
                                    return@TextButton
                                }
                            }
                        }

                        // Convertir hora de 24h a 12h con AM/PM
                        val ampm = if (h < 12) "AM" else "PM"
                        val h12 = when (h) {
                            0 -> 12
                            12 -> 12
                            else -> if (h < 12) h else h - 12
                        }
                        hora = String.format("%d:%02d %s", h12, m, ampm)
                        mostrarReloj = false
                    }
                ) {
                    Text("OK", color = AzulMedio)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarReloj = false }) {
                    Text("Cancelar", color = AzulMedio)
                }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    // DIÁLOGO DE MENSAJES
    if (mostrarMensaje) {
        AlertDialog(
            onDismissRequest = {
                mostrarMensaje = false
                if (esExito) {
                    navController.popBackStack()
                }
            },
            title = { Text(if (esExito) "Éxito" else "Error") },
            text = { Text(mensaje) },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarMensaje = false
                        if (esExito) {
                            navController.popBackStack()
                        }
                    }
                ) {
                    Text("OK", color = AzulMedio)
                }
            }
        )
    }

    // ESTRUCTURA PRINCIPAL
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Reserva") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Nombre
            Text("Nombre")
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Apellido
            Text("Apellido")
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Fecha
            Text("Fecha")
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("DD/MM/AAAA") },
                trailingIcon = {
                    TextButton(
                        onClick = { mostrarCalendario = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = AzulMedio
                        )
                    ) {
                        Text("📅")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Hora
            Text("Hora")
            OutlinedTextField(
                value = hora,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("HH:MM AM/PM") },
                trailingIcon = {
                    TextButton(
                        onClick = { mostrarReloj = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = AzulMedio
                        )
                    ) {
                        Text("🕐")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Pista
            Text("Número de Pista")
            ExposedDropdownMenuBox(
                expanded = expandedPista,
                onExpandedChange = { expandedPista = !expandedPista }
            ) {
                OutlinedTextField(
                    value = pista,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedPista,
                    onDismissRequest = { expandedPista = false }
                ) {
                    (1..8).map { it.toString() }.forEach {
                        DropdownMenuItem(
                            text = { Text("Pista $it") },
                            onClick = { pista = it; expandedPista = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTONES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Guardar
                Button(
                    onClick = {
                        when {
                            nombre.isBlank() || apellido.isBlank() -> {
                                mensaje = "Nombre y apellido son obligatorios"
                                esExito = false
                                mostrarMensaje = true
                            }
                            fecha.isBlank() -> {
                                mensaje = "Debe seleccionar una fecha"
                                esExito = false
                                mostrarMensaje = true
                            }
                            hora.isBlank() -> {
                                mensaje = "Debe seleccionar una hora"
                                esExito = false
                                mostrarMensaje = true
                            }
                            else -> {
                                try {
                                    // Convertir fecha
                                    val fechaParts = fecha.split("/")
                                    val fechaSQL = "${fechaParts[2]}-${fechaParts[1].padStart(2, '0')}-${fechaParts[0].padStart(2, '0')}"

                                    // Convertir hora
                                    val horaParts = hora.split(" ")
                                    val timeParts = horaParts[0].split(":")
                                    var hora24 = timeParts[0].toInt()
                                    val minutos = timeParts[1]
                                    val ampm = horaParts[1]

                                    if (ampm == "PM" && hora24 != 12) {
                                        hora24 += 12
                                    } else if (ampm == "AM" && hora24 == 12) {
                                        hora24 = 0
                                    }

                                    val horaSQL = String.format("%02d:%02d:00", hora24, minutos.toInt())

                                    // Verificar disponibilidad
                                    val disponible = reservaDAO.verificarDisponibilidad(
                                        pista.toInt(),
                                        fechaSQL,
                                        horaSQL
                                    )

                                    if (!disponible) {
                                        mensaje = "La pista ya está reservada en esa fecha y hora"
                                        esExito = false
                                        mostrarMensaje = true
                                    } else {
                                        val resultado = reservaDAO.crearReservaCompleta(
                                            nombreCliente = nombre,
                                            apellidoCliente = apellido,
                                            pistaId = pista.toInt(),
                                            fecha = fechaSQL,
                                            hora = horaSQL,
                                            estadoId = 1
                                        )

                                        if (resultado > 0) {
                                            mensaje = "Reserva creada exitosamente"
                                            esExito = true
                                            mostrarMensaje = true
                                        } else {
                                            mensaje = "Error al crear la reserva"
                                            esExito = false
                                            mostrarMensaje = true
                                        }
                                    }
                                } catch (e: Exception) {
                                    mensaje = "Error: ${e.message}"
                                    esExito = false
                                    mostrarMensaje = true
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulMedio
                    )
                ) {
                    Text("Guardar", color = Color.White)
                }

                // Botón Cancelar
                OutlinedButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AzulMedio
                    )
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}