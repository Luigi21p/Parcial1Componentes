package com.ud.parcial1componentes.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(navController: NavController) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val reservaDAO = remember { ReservaDAO(dbHelper) }

    // Cargar datos
    val todasLasReservas = remember { reservaDAO.obtenerTodasLasReservas() }
    val ocupacionPorPista = remember { reservaDAO.obtenerResumenOcupacion() }

    // Calcular estadísticas
    val totalReservas = todasLasReservas.size
    val reservasActivas = todasLasReservas.count { it.estadoId == 1 }
    val reservasCanceladas = todasLasReservas.count { it.estadoId == 2 }
    val reservasCompletadas = todasLasReservas.count { it.estadoId == 3 }

    // Pista más ocupada hoy
    val pistaMasOcupada = ocupacionPorPista.maxByOrNull { it.value }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Resumen de Reservas",
                        fontWeight = FontWeight.Bold,
                        color = BlancoSuave
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = BlancoSuave
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulOscuro
                )
            )
        },
        containerColor = AzulOscuro
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Tarjeta principal: Total general ──────────────────────────
            TarjetaTotal(totalReservas = totalReservas)

            // ── Fila de estadísticas ──────────────────────────────────────
            Text(
                text = "Estado de reservas",
                color = BlancoSuave.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TarjetaEstado(
                    modifier = Modifier.weight(1f),
                    titulo = "Activas",
                    cantidad = reservasActivas,
                    color = Verde,
                    icono = Icons.Default.CheckCircle
                )
                TarjetaEstado(
                    modifier = Modifier.weight(1f),
                    titulo = "Canceladas",
                    cantidad = reservasCanceladas,
                    color = Rojo,
                    icono = Icons.Default.Close
                )
                TarjetaEstado(
                    modifier = Modifier.weight(1f),
                    titulo = "Completas",
                    cantidad = reservasCompletadas,
                    color = Naranja,
                    icono = Icons.Default.DateRange
                )
            }

            // ── Ocupación de pistas hoy ───────────────────────────────────
            Text(
                text = "Ocupación de pistas hoy",
                color = BlancoSuave.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GrisTarjeta)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (ocupacionPorPista.isEmpty()) {
                        Text(
                            text = "Sin datos de pistas",
                            color = BlancoSuave.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        val maxValor = ocupacionPorPista.values.maxOrNull()?.coerceAtLeast(1) ?: 1
                        ocupacionPorPista.entries.forEachIndexed { index, (pista, cantidad) ->
                            if (index > 0) Spacer(modifier = Modifier.height(10.dp))
                            FilaPista(
                                nombrePista = pista,
                                cantidad = cantidad,
                                maxValor = maxValor
                            )
                        }
                    }
                }
            }

            // ── Pista destacada del día ───────────────────────────────────
            if (pistaMasOcupada != null && pistaMasOcupada.value > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(AzulMedio, AzulAcento.copy(alpha = 0.7f))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                "🏆 Más activa hoy",
                                color = BlancoSuave.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                pistaMasOcupada.key,
                                color = BlancoSuave,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${pistaMasOcupada.value} reserva(s) activa(s)",
                                color = BlancoSuave.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // ── Últimas 3 reservas ────────────────────────────────────────
            if (todasLasReservas.isNotEmpty()) {
                Text(
                    text = "Últimas reservas registradas",
                    color = BlancoSuave.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                todasLasReservas.take(3).forEach { reserva ->
                    TarjetaMiniReserva(reserva = reserva)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Tarjeta total general ─────────────────────────────────────────────────────
@Composable
private fun TarjetaTotal(totalReservas: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(AzulMedio, AzulAcento)),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = BlancoSuave.copy(alpha = 0.8f),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$totalReservas",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BlancoSuave
                )
                Text(
                    text = "Total de reservas",
                    fontSize = 14.sp,
                    color = BlancoSuave.copy(alpha = 0.75f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ── Tarjeta de estado (activa/cancelada/completada) ───────────────────────────
@Composable
private fun TarjetaEstado(
    modifier: Modifier = Modifier,
    titulo: String,
    cantidad: Int,
    color: Color,
    icono: ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GrisTarjeta)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$cantidad",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = titulo,
                fontSize = 11.sp,
                color = BlancoSuave.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Fila de barra de progreso por pista ──────────────────────────────────────
@Composable
private fun FilaPista(nombrePista: String, cantidad: Int, maxValor: Int) {
    val progreso = cantidad.toFloat() / maxValor.toFloat()
    val animado by animateFloatAsState(
        targetValue = progreso,
        animationSpec = tween(durationMillis = 800),
        label = "barra_$nombrePista"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = nombrePista,
                color = BlancoSuave,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (cantidad == 0) "Libre" else "$cantidad reserva(s)",
                color = if (cantidad == 0) Verde else AzulAcento,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(AzulMedio)
        ) {
            if (cantidad == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animado)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(Verde.copy(alpha = 0.3f))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animado)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(Brush.horizontalGradient(listOf(AzulAcento, Verde)))
                )
            }
        }
    }
}

// ── Mini tarjeta de reserva reciente ─────────────────────────────────────────
@Composable
private fun TarjetaMiniReserva(reserva: Reserva) {
    val colorEstado = when (reserva.estadoId) {
        1 -> Verde
        2 -> Rojo
        else -> Naranja
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GrisTarjeta)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de color de estado
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colorEstado)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reserva.clienteCompleto,
                    color = BlancoSuave,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "${reserva.pistaNombre} · ${reserva.fechaFormateada} ${reserva.horaFormateada}",
                    color = BlancoSuave.copy(alpha = 0.55f),
                    fontSize = 12.sp
                )
            }
            Text(
                text = reserva.estadoNombre,
                color = colorEstado,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}