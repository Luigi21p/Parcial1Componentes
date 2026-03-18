package com.ud.parcial1componentes.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// controla la navegación entre pantallas
@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // Pantalla principal con el menú
        composable("home") {
            HomeScreen(navController)
        }

        // Formulario para crear una nueva reserva
        composable("crear") {
            CrearReservaScreen(navController = navController)
        }

        // Listado de reservas con búsqueda, edición y eliminación
        composable("lista") {
            ListadoReservasScreen(navController = navController)
        }

        // ── NUEVO: Pantalla de resumen de ocupación ───────────────────────
        composable("resumen") {
            ResumenScreen(navController = navController)
        }

        // Formulario para editar una reserva existente
        composable("editar/{reservaId}") { backStackEntry ->

            val reservaId = backStackEntry.arguments
                ?.getString("reservaId")
                ?.toInt()
                ?: 0

            EditarReservaScreen(
                navController = navController,
                reservaId = reservaId
            )
        }
    }
}