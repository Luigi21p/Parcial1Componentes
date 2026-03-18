package com.ud.parcial1componentes.logica

// Guarda la información de una reserva de pista de bowling
data class Reserva(
    // Datos que se guardan en la base de datos
    val id: Int = 0,
    val clienteId: Int,
    val pistaId: Int,
    val fecha: String,
    val hora: String,
    val estadoId: Int,           // ID del estado: 1=Activa, 2=Cancelada, 3=Completada

    // Datos adicionales que vienen de otras tablas (cuando hacemos búsquedas)
    var clienteNombre: String = "",
    var clienteApellido: String = "",
    var pistaNombre: String = "",
    var estadoNombre: String = ""
) {
    // PROPIEDADES HELPER (ayudan a mostrar la información)

    // Junta nombre y apellido del cliente
    val clienteCompleto: String
        get() = "$clienteNombre $clienteApellido"

    // Junta fecha y hora
    val fechaHora: String
        get() = "$fecha $hora"

    // Dice si la reserva está activa o no
    val estaActiva: Boolean
        get() = estadoNombre == "Activa" || estadoId == 1

    // Convierte la fecha de la base de datos (YYYY-MM-DD) a formato legible (DD/MM/YYYY)
    val fechaFormateada: String
        get() {
            return try {
                val partes = fecha.split("-")
                val dia = partes[2]
                val mes = partes[1]
                val año = partes[0]
                "$dia/$mes/$año"
            } catch (e: Exception) {
                fecha  // Si hay error, muestra la fecha original
            }
        }

    // Convierte la hora de 24h (HH:MM:SS) a 12h con AM/PM
    val horaFormateada: String
        get() {
            return try {
                val partes = hora.split(":")
                val hora24 = partes[0].toInt()
                val minutos = partes[1]

                // Determina si es AM o PM
                val ampm = if (hora24 < 12) "AM" else "PM"

                // Convierte a formato 12h
                val hora12 = when {
                    hora24 == 0 -> 12
                    hora24 == 12 -> 12
                    hora24 < 12 -> hora24
                    else -> hora24 - 12
                }

                "$hora12:$minutos $ampm"
            } catch (e: Exception) {
                hora  // Si hay error, muestra la hora original
            }
        }
}