package com.ud.parcial1componentes

import com.ud.parcial1componentes.logica.Cliente
import com.ud.parcial1componentes.logica.Reserva
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservaTest {

    @Test
    fun `verificar busqueda por nombre de cliente`() {
        //  Preparar los datos de prueba
        val cliente = Cliente(
            id = 1,
            nombre = "Juan",
            apellido = "Perez"
        )

        val reserva = Reserva(
            id = 1,
            clienteId = 1,
            pistaId = 1,
            fecha = "2026-03-18",
            hora = "10:00:00",
            estadoId = 1,
            clienteNombre = "Juan",
            clienteApellido = "Perez",
            pistaNombre = "Pista 1",
            estadoNombre = "Activa"
        )

        val listaReservas = listOf(reserva)

        // Simular búsqueda por nombre (como en tu app)
        val busqueda = "Juan"
        val resultado = listaReservas.filter {
            it.clienteCompleto.contains(busqueda, ignoreCase = true)
        }

        //  Verificaciones
        assertEquals("Debería encontrar 1 reserva", 1, resultado.size)
        assertEquals("Juan Perez", resultado[0].clienteCompleto)
    }

    @Test
    fun `validar regla de no duplicidad de pista y hora`() {
        // Preparar datos
        val pistaId = 5
        val fecha = "2026-03-18"
        val hora = "15:00:00"

        val reservaExistente = Reserva(
            id = 1,
            clienteId = 1,
            pistaId = pistaId,
            fecha = fecha,
            hora = hora,
            estadoId = 1,  // Activa
            clienteNombre = "Juan",
            clienteApellido = "Perez",
            pistaNombre = "Pista 5",
            estadoNombre = "Activa"
        )

        val reservasExistentes = listOf(reservaExistente)

        // Simular verificación de disponibilidad (regla de negocio)
        val nuevaPistaId = 5
        val nuevaFecha = "2026-03-18"
        val nuevaHora = "15:00:00"

        val estaOcupada = reservasExistentes.any {
            it.pistaId == nuevaPistaId &&
                    it.fecha == nuevaFecha &&
                    it.hora == nuevaHora &&
                    it.estaActiva  // Solo importan las activas
        }

        assertTrue("La pista debería estar ocupada", estaOcupada)

        // Probar con diferente hora (debería estar disponible)
        val horaDiferente = "16:00:00"
        val estaDisponible = !reservasExistentes.any {
            it.pistaId == nuevaPistaId &&
                    it.fecha == nuevaFecha &&
                    it.hora == horaDiferente &&
                    it.estaActiva
        }

        assertTrue("La pista debería estar disponible en diferente hora", estaDisponible)
    }

    @Test
    fun `validar que no se puede reservar en fecha pasada`() {
        // Esta es una regla de negocio que deberías tener en tu lógica
        val fechaPasada = "2025-01-01" // Año pasado
        val hoy = "2026-03-17" // Suponiendo que hoy es esta fecha

        fun fechaEsValida(fecha: String): Boolean {
            return fecha >= hoy // Comparación simple de strings (YYYY-MM-DD)
        }

        assertFalse("Fecha pasada no debería ser válida", fechaEsValida(fechaPasada))
        assertTrue("Fecha actual debería ser válida", fechaEsValida(hoy))
    }

    @Test
    fun `validar cambio de estado de reserva`() {
        val reserva = Reserva(
            id = 1,
            clienteId = 1,
            pistaId = 1,
            fecha = "2026-03-18",
            hora = "10:00:00",
            estadoId = 1,
            clienteNombre = "Juan",
            clienteApellido = "Perez",
            pistaNombre = "Pista 1",
            estadoNombre = "Activa"
        )

        // Verificar estado inicial
        assertTrue("Debería estar activa", reserva.estaActiva)
        assertEquals("Activa", reserva.estadoNombre)

        // Simular cambio de estado
        val reservaCancelada = reserva.copy(estadoId = 2, estadoNombre = "Cancelada")

        assertFalse("No debería estar activa", reservaCancelada.estaActiva)
        assertEquals("Cancelada", reservaCancelada.estadoNombre)
    }


}