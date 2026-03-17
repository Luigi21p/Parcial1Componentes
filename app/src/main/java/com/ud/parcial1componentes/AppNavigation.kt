package com.ud.parcial1componentes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import androidx.navigation.NavHostController

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(navController)
        }

        composable("crear") {
            CrearReservaScreen()
        }

        composable("lista") {
            ListadoReservasScreen()
        }

    }
}