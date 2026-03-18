package com.ud.parcial1componentes.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Paleta de colores
private val AzulOscuro    = Color(0xFF0D1B2A)
private val AzulMedio     = Color(0xFF1B3A5C)
private val AzulAcento    = Color(0xFF2196F3)
private val AzulClaro     = Color(0xFF64B5F6)
private val BlancoSuave   = Color(0xFFF0F4F8)
private val GrisTarjeta   = Color(0xFF1E2D3D)
private val GrisBorde     = Color(0xFF2A3F55)

@Composable
fun HomeScreen(navController: NavController) {

    // Animación de entrada al aparecer la pantalla
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600),
        label = "entrada"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulOscuro)
            .graphicsLayer { this.alpha = alpha }
    ) {

        // Fondo decorativo: círculo grande difuminado
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = 80.dp, y = (-80).dp)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.radialGradient(
                        listOf(AzulAcento.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
                .align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Encabezado ────────────────────────────────────────────────
            Text(
                text = "🎳",
                fontSize = 56.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Bowling Club",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BlancoSuave,
                letterSpacing = 1.sp
            )
            Text(
                text = "Gestión de Reservas",
                fontSize = 14.sp,
                color = AzulClaro.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Divisor decorativo ────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = GrisBorde
                )
                Text(
                    text = "  MENÚ  ",
                    color = AzulClaro.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    letterSpacing = 3.sp
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = GrisBorde
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Botones de navegación ─────────────────────────────────────
            BotonMenu(
                texto = "Nueva Reserva",
                subtexto = "Registrar una reserva de pista",
                icono = Icons.Default.AddCircle,
                colorIcono = AzulAcento,
                esPrimario = true,
                onClick = { navController.navigate("crear") }
            )

            Spacer(modifier = Modifier.height(14.dp))

            BotonMenu(
                texto = "Ver Reservas",
                subtexto = "Listar, buscar y editar reservas",
                icono = Icons.Default.List,
                colorIcono = AzulClaro,
                esPrimario = false,
                onClick = { navController.navigate("lista") }
            )

            Spacer(modifier = Modifier.height(14.dp))

            BotonMenu(
                texto = "Resumen General",
                subtexto = "Estadísticas y ocupación de pistas",
                icono = Icons.Default.Star,
                colorIcono = Color(0xFFFFB300),
                esPrimario = false,
                onClick = { navController.navigate("resumen") }
            )

            Spacer(modifier = Modifier.height(56.dp))

            // ── Pie de página ─────────────────────────────────────────────
            Text(
                text = "Programación por Componentes · 2026-1",
                color = BlancoSuave.copy(alpha = 0.2f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Componente reutilizable de botón del menú ─────────────────────────────────
@Composable
private fun BotonMenu(
    texto: String,
    subtexto: String,
    icono: ImageVector,
    colorIcono: Color,
    esPrimario: Boolean,
    onClick: () -> Unit
) {
    val fondo = if (esPrimario)
        Brush.horizontalGradient(listOf(AzulMedio, AzulAcento.copy(alpha = 0.9f)))
    else
        Brush.horizontalGradient(listOf(GrisTarjeta, GrisTarjeta))

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = if (esPrimario) 6.dp else 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(fondo)
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícono con fondo circular
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorIcono.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = colorIcono,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Texto
                Column {
                    Text(
                        text = texto,
                        color = BlancoSuave,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = subtexto,
                        color = BlancoSuave.copy(alpha = 0.55f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}