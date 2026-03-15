package com.ud.parcial1componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearReservaScreen() {

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var pista by remember { mutableStateOf("1") }
    var expandedPista by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }
    var mostrarReloj by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val cal = java.util.Calendar.getInstance()
                        cal.timeInMillis = it
                        fecha = "${cal.get(java.util.Calendar.DAY_OF_MONTH)}/" +
                                "${cal.get(java.util.Calendar.MONTH) + 1}/" +
                                "${cal.get(java.util.Calendar.YEAR)}"
                    }
                    mostrarCalendario = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (mostrarReloj) {
        AlertDialog(
            onDismissRequest = { mostrarReloj = false },
            confirmButton = {
                TextButton(onClick = {
                    val h = timePickerState.hour
                    val m = timePickerState.minute
                    val ampm = if (h < 12) "AM" else "PM"
                    val h12 = if (h % 12 == 0) 12 else h % 12
                    hora = "${h12}:${m.toString().padStart(2, '0')} $ampm"
                    mostrarReloj = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarReloj = false }) { Text("Cancelar") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Nueva Reserva") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Campos de Cliente
            Text("Nombre")
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Apellido")
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Fecha de Reserva
            Text("Fecha")
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("DD/MM/AAAA") },
                trailingIcon = {
                    TextButton(onClick = { mostrarCalendario = true }) { Text("📅") }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Hora de Reserva
            Text("Hora")
            OutlinedTextField(
                value = hora,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("HH:MM") },
                trailingIcon = {
                    TextButton(onClick = { mostrarReloj = true }) { Text("🕐") }
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
                    listOf("1","2","3","4","5","6").forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = { pista = it; expandedPista = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = {}, modifier = Modifier.weight(1f)) { Text("Guardar") }
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Cancelar") }
            }
        }
    }
}