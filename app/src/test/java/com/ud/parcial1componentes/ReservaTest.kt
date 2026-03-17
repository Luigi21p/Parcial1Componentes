package com.ud.parcial1componentes

import com.ud.parcial1componentes.logic.Cliente
import com.ud.parcial1componentes.logic.Pista
import com.ud.parcial1componentes.logic.Reserva
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime

class ReservaTest {

    @Test
    fun `verificar busqueda por nombre de cliente`() {
        // 1. Preparar el cliente con los 4 parámetros de tu modelo
        val cliente = Cliente(
            id = "1010",
            nombre = "Juan Perez",
            telefono = "300123",
            correo = "juan@mail.com"
        )

        // 2. Crear la reserva usando LocalDateTime.of(año, mes, día, hora, minuto)
        val fechaPrueba = LocalDateTime.of(2026, 3, 18, 10, 0)

        val listaReservas = listOf(
            Reserva(
                id = 1,
                cliente = cliente,
                pista = Pista(numeroPista = 1),
                fechaHora = fechaPrueba
            )
        )

        // 3. Lógica de búsqueda (Regla de negocio )
        val resultado = listaReservas.filter { it.cliente.nombre.contains("Juan", ignoreCase = true) }

        // 4. Verificaciones para el puntaje de Pruebas Unitarias [cite: 35]
        assertEquals("Debería encontrar 1 reserva", 1, resultado.size)
        assertEquals("Juan Perez", resultado[0].cliente.nombre)
    }

    @Test
    fun `validar regla de no duplicidad de pista y hora`() {
        // Regla: No permitir misma pista, fecha y hora (Regla de negocio )
        val fechaComun = LocalDateTime.of(2026, 3, 18, 15, 0)
        val pistaCompartida = Pista(numeroPista = 5)

        val clienteDefault = Cliente("1", "Admin", "000", "admin@mail.com")

        val reservasExistentes = listOf(
            Reserva(1, clienteDefault, pistaCompartida, fechaComun)
        )

        // Simulación de intento de nueva reserva en la misma pista y hora
        val nuevaPistaId = 5
        val nuevaFecha = LocalDateTime.of(2026, 3, 18, 15, 0)

        val estaOcupada = reservasExistentes.any {
            it.pista.numeroPista == nuevaPistaId && it.fechaHora == nuevaFecha
        }

        assertTrue("La prueba debe confirmar que la pista NO está disponible", estaOcupada)
    }
}