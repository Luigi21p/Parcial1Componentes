package com.ud.parcial1componentes.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// controla la navegación entre pantallas
@Composable
fun AppNavigation(navController: NavHostController) {

    // NavHost = Contenedor de navegación
    // startDestination = "home" significa que la primera pantalla será HomeScreen
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // Muestra la pantalla principal con el menú
        composable("home") {
            HomeScreen(navController)
        }

        // Muestra el formulario para crear una nueva reserva
        composable("crear") {
            CrearReservaScreen(navController = navController)
        }

        // Muestra todas las reservas con opciones de buscar, editar y eliminar
        composable("lista") {
            ListadoReservasScreen(navController = navController)
        }

        // Muestra el formulario para editar una reserva existente
        composable("editar/{reservaId}") { backStackEntry ->

            // Obtener el ID de la reserva desde la URL
            // "editar/5" → reservaId = 5
            val reservaId = backStackEntry.arguments
                ?.getString("reservaId")   // Toma el valor de {reservaId}
                ?.toInt()                   // Lo convierte a número entero
                ?: 0                         // Si no hay ID, usa 0

            // Llama a la pantalla de editar pasándole el ID
            EditarReservaScreen(
                navController = navController,
                reservaId = reservaId
            )
        }
    }
}