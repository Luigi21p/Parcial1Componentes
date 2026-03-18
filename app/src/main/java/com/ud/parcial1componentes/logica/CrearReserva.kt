package com.ud.parcial1componentes.logica

import com.ud.parcial1componentes.persistencia.ReservaDAO
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

// Esta clase guarda si una operación fue exitosa o tuvo error

sealed class ResultadoOperacion {
    // salio bien
    data class Exito(val mensaje: String) : ResultadoOperacion()

    // salio mal
    data class Error(val mensaje: String) : ResultadoOperacion()
}

// Esta clase se encarga de crear nuevas reservas
// Verifica que todos los datos estén correctos antes de guardar
class CrearReserva(private val reservaDAO: ReservaDAO) {

    // Recibe todos los datos de la reserva y devuelve si funcionó o no
    fun crearNuevaReserva(
        nombreCliente: String,
        apellidoCliente: String,
        pistaId: Int,
        fecha: String,                // Fecha en formato YYYY-MM-DD
        hora: String,                 // Hora en formato HH:MM:SS
        estadoId: Int = 1              // Estado: 1=Activa (por defecto)
    ): ResultadoOperacion {

        // Validar que el nombre y apellido no estén vacíos
        if (nombreCliente.isBlank() || apellidoCliente.isBlank()) {
            return ResultadoOperacion.Error("El nombre y apellido del cliente son obligatorios")
        }

        // Validar que la fecha tenga el formato correcto
        if (!validarFormatoFecha(fecha)) {
            return ResultadoOperacion.Error("Formato de fecha inválido")
        }

        // Validar que la hora tenga el formato correcto
        if (!validarFormatoHora(hora)) {
            return ResultadoOperacion.Error("Formato de hora inválido")
        }

        // Validar que la pista sea válida (1-8)
        if (pistaId < 1 || pistaId > 8) {
            return ResultadoOperacion.Error("Número de pista inválido")
        }

        // Validar que la fecha no sea anterior a hoy
        if (!validarFechaNoPasada(fecha, hora)) {
            return ResultadoOperacion.Error("No se pueden hacer reservas en fecha y hora pasadas")
        }

        // Validar que la pista esté disponible en esa fecha y hora
        if (!reservaDAO.verificarDisponibilidad(pistaId, fecha, hora)) {
            return ResultadoOperacion.Error("La pista ya está reservada en esa fecha y hora")
        }

        // Guardar la reserva en la base de datos
        val resultado = reservaDAO.crearReservaCompleta(
            nombreCliente = nombreCliente,
            apellidoCliente = apellidoCliente,
            pistaId = pistaId,
            fecha = fecha,
            hora = hora,
            estadoId = estadoId
        )

        // Devolver el resultado
        // Si resultado > 0 significa que se guardó correctamente
        return if (resultado > 0) {
            ResultadoOperacion.Exito("Reserva creada exitosamente")
        } else {
            ResultadoOperacion.Error("Error al crear la reserva")
        }
    }

    // valida que la fecha y hora NO sean anteriores a ahora
    private fun validarFechaNoPasada(fecha: String, hora: String): Boolean {
        return try {
            // Comparar la fecha
            val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            formato.timeZone = TimeZone.getDefault()  // Usa la hora del celular
            val fechaReserva = formato.parse("$fecha $hora")

            // Obtiene la fecha y hora actual del celular
            val ahora = Calendar.getInstance().time

            //  válida solo si es despues de ahora
            fechaReserva.after(ahora)

        } catch (e: Exception) {
            // Si hay error al comparar, no es válida
            false
        }
    }

    // VALIDA que la fecha tenga el formato correcto: YYYY-MM-DD
    private fun validarFormatoFecha(fecha: String): Boolean {
        return try {
            val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formato.isLenient = false  // No permite fechas como 2026-13-45
            formato.parse(fecha)
            true  // Si no hay error, el formato es correcto
        } catch (e: Exception) {
            false  // Si hay error, el formato es incorrecto
        }
    }

    // Igual para la hora
    private fun validarFormatoHora(hora: String): Boolean {
        return try {
            val partes = hora.split(":")

            if (partes.size != 3) return false

            val horas = partes[0].toInt()
            val minutos = partes[1].toInt()
            val segundos = partes[2].toInt()

            // Verifica que los valores estén en rango válido
            horas in 0..23 && minutos in 0..59 && segundos in 0..59

        } catch (e: Exception) {
            false  // Si hay error, el formato es incorrecto
        }
    }
}