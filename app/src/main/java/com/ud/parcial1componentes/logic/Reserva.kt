package com.ud.parcial1componentes.logic
import java.time.LocalDateTime

data class Reserva(
    val id: Int,
    val cliente: Cliente,
    val pista: Pista,
    val fechaHora: LocalDateTime,
    var estado: EstadoReserva = EstadoReserva.ACTIVA
)

enum class EstadoReserva {
    ACTIVA, CANCELADA, COMPLETADA
}
