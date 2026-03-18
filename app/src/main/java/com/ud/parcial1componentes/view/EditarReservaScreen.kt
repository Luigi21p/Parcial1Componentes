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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

// Esta pantalla permite modificar una reserva existente
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarReservaScreen(
    navController: NavController,
    reservaId: Int
) {

    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context)
    val reservaDAO = ReservaDAO(dbHelper)

    // ============================================
    // VARIABLES DE ESTADO
    // ============================================
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var pista by remember { mutableStateOf("1") }
    var estado by remember { mutableStateOf("1") }

    var expandedPista by remember { mutableStateOf(false) }
    var expandedEstado by remember { mutableStateOf(false) }

    var mostrarCalendario by remember { mutableStateOf(false) }
    var mostrarReloj by remember { mutableStateOf(false) }

    var mostrarMensaje by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var esExito by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    // ============================================
    // CARGA DE DATOS
    // ============================================
    LaunchedEffect(Unit) {
        val reservas = reservaDAO.obtenerTodasLasReservas()
        val reserva = reservas.find { it.id == reservaId }

        reserva?.let {
            val partes = it.fecha.split("-")
            fecha = "${partes[2]}/${partes[1]}/${partes[0]}"

            val horaPartes = it.hora.split(":")
            val hora24 = horaPartes[0].toInt()
            val minutos = horaPartes[1]
            val ampm = if (hora24 < 12) "AM" else "PM"
            val h12 = when (hora24) {
                0 -> 12
                12 -> 12
                else -> if (hora24 < 12) hora24 else hora24 - 12
            }
            hora = String.format("%d:%02d %s", h12, minutos.toInt(), ampm)

            pista = it.pistaNombre.replace("Pista ", "")
            estado = it.estadoId.toString()
        }
    }

    // ============================================
    // DIÁLOGO DEL CALENDARIO (CORREGIDO)
    // ============================================
    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { fechaSeleccionada ->

                            // PASO 1: Obtener la fecha en UTC del DatePicker
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            val fechaUTC = sdf.format(Date(fechaSeleccionada))

                            // PASO 2: Convertir a zona local del dispositivo
                            val sdfLocal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            sdfLocal.timeZone = TimeZone.getDefault()
                            val fechaLocal = sdfLocal.parse(fechaUTC)

                            // PASO 3: Usar Calendar con la fecha local
                            val cal = Calendar.getInstance()
                            cal.time = fechaLocal

                            // PASO 4: Obtener fecha actual (local)
                            val calHoy = Calendar.getInstance()
                            calHoy.set(Calendar.HOUR_OF_DAY, 0)
                            calHoy.set(Calendar.MINUTE, 0)
                            calHoy.set(Calendar.SECOND, 0)
                            calHoy.set(Calendar.MILLISECOND, 0)

                            // PASO 5: Comparar fechas locales
                            if (cal.timeInMillis >= calHoy.timeInMillis) {
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
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ============================================
    // DIÁLOGO DEL RELOJ
    // ============================================
    if (mostrarReloj) {
        AlertDialog(
            onDismissRequest = { mostrarReloj = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val h = timePickerState.hour
                        val m = timePickerState.minute

                        // Validar hora actual si es el mismo día
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

                            // Si es hoy, no permitir horas pasadas
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

                        // Convertir a formato 12h AM/PM
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
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarReloj = false }) {
                    Text("Cancelar")
                }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    // ============================================
    // DIÁLOGO DE MENSAJES
    // ============================================
    if (mostrarMensaje) {
        AlertDialog(
            onDismissRequest = {
                mostrarMensaje = false
                if (esExito) navController.popBackStack()
            },
            title = { Text(if (esExito) "Éxito" else "Error") },
            text = { Text(mensaje) },
            confirmButton = {
                TextButton(onClick = {
                    mostrarMensaje = false
                    if (esExito) navController.popBackStack()
                }) {
                    Text("OK")
                }
            }
        )
    }

    // ============================================
    // ESTRUCTURA PRINCIPAL
    // ============================================
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Editar Reserva") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Fecha")
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    TextButton(onClick = { mostrarCalendario = true }) { Text("📅") }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Hora")
            OutlinedTextField(
                value = hora,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    TextButton(onClick = { mostrarReloj = true }) { Text("🕐") }
                },
                modifier = Modifier.fillMaxWidth()
            )

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

            Text("Estado")
            ExposedDropdownMenuBox(
                expanded = expandedEstado,
                onExpandedChange = { expandedEstado = !expandedEstado }
            ) {
                OutlinedTextField(
                    value = when (estado) {
                        "1" -> "Activa"
                        "2" -> "Cancelada"
                        "3" -> "Completada"
                        else -> "Activa"
                    },
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedEstado,
                    onDismissRequest = { expandedEstado = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Activa") },
                        onClick = { estado = "1"; expandedEstado = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Cancelada") },
                        onClick = { estado = "2"; expandedEstado = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Completada") },
                        onClick = { estado = "3"; expandedEstado = false }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        try {
                            val fechaParts = fecha.split("/")

                            // Validar fecha
                            val calReserva = Calendar.getInstance()
                            calReserva.set(fechaParts[2].toInt(), fechaParts[1].toInt() - 1, fechaParts[0].toInt())

                            val calHoy = Calendar.getInstance()
                            calHoy.set(Calendar.HOUR_OF_DAY, 0)
                            calHoy.set(Calendar.MINUTE, 0)
                            calHoy.set(Calendar.SECOND, 0)
                            calHoy.set(Calendar.MILLISECOND, 0)

                            if (calReserva.timeInMillis < calHoy.timeInMillis) {
                                mensaje = "No se pueden seleccionar fechas anteriores a hoy"
                                esExito = false
                                mostrarMensaje = true
                                return@Button
                            }

                            // Convertir fecha a formato BD
                            val fechaSQL = "${fechaParts[2]}-${fechaParts[1].padStart(2, '0')}-${fechaParts[0].padStart(2, '0')}"

                            // Convertir hora
                            val horaParts = hora.split(" ")
                            val timeParts = horaParts[0].split(":")
                            var hora24 = timeParts[0].toInt()
                            val minutos = timeParts[1]
                            val ampm = horaParts[1]

                            if (ampm == "PM" && hora24 != 12) hora24 += 12
                            else if (ampm == "AM" && hora24 == 12) hora24 = 0

                            val horaSQL = String.format("%02d:%02d:00", hora24, minutos.toInt())

                            // Verificar disponibilidad
                            val disponible = reservaDAO.verificarDisponibilidadParaEditar(
                                reservaId, pista.toInt(), fechaSQL, horaSQL
                            )

                            if (!disponible) {
                                mensaje = "La pista ya está reservada en esa fecha y hora"
                                esExito = false
                                mostrarMensaje = true
                            } else {
                                val resultado = reservaDAO.actualizarReserva(
                                    reservaId = reservaId,
                                    pistaId = pista.toInt(),
                                    fecha = fechaSQL,
                                    hora = horaSQL,
                                    estadoId = estado.toInt()
                                )

                                if (resultado > 0) {
                                    mensaje = "Reserva actualizada exitosamente"
                                    esExito = true
                                    mostrarMensaje = true
                                } else {
                                    mensaje = "Error al actualizar"
                                    esExito = false
                                    mostrarMensaje = true
                                }
                            }
                        } catch (e: Exception) {
                            mensaje = "Error: ${e.message}"
                            esExito = false
                            mostrarMensaje = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Actualizar")
                }

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}