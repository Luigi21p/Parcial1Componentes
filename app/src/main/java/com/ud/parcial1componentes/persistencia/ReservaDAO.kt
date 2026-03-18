package com.ud.parcial1componentes.persistencia

import android.content.ContentValues
import com.ud.parcial1componentes.logica.Reserva

// se crean, buscan, actualizan y eliminan las reservas
class ReservaDAO(private val dbHelper: DatabaseHelper) {

    //  Crear una reserva completa
    // Crea una nueva reserva. Si el cliente no existe, lo crea automáticamente
    fun crearReservaCompleta(
        nombreCliente: String,
        apellidoCliente: String,
        pistaId: Int,
        fecha: String,
        hora: String,
        estadoId: Int = 1
    ): Long {
        val db = dbHelper.writableDatabase
        var resultado: Long = -1  // -1 significa error

        // beginTransaction = hacer varias operaciones juntas"
        // Si algo falla, no se guarda nada
        db.beginTransaction()
        try {
            // Insertar o obtener el cliente
            val clienteDAO = ClienteDAO(dbHelper)
            val clienteId = clienteDAO.insertarObtenerCliente(nombreCliente, apellidoCliente)

            //  Insertar la reserva con el ID del cliente
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_CLIENTE_ID, clienteId)
                put(DatabaseHelper.COLUMN_PISTA_ID, pistaId)
                put(DatabaseHelper.COLUMN_FECHA, fecha)
                put(DatabaseHelper.COLUMN_HORA, hora)
                put(DatabaseHelper.COLUMN_ESTADO_ID, estadoId)
            }

            resultado = db.insert(DatabaseHelper.TABLE_RESERVA, null, values)
            db.setTransactionSuccessful()  // bien, guardar cambios
        } catch (e: Exception) {
            e.printStackTrace()  // Algo falló, no se guarda nada
        } finally {
            db.endTransaction()  // Terminar la transacción
        }

        return resultado  // Devuelve el ID de la nueva reserva o -1
    }
    // Verificar disponibilidad de pista

    // Revisa si una pista está disponible en una fecha y hora específicas
    // Devuelve true si está disponible, false si ya hay una reserva
    fun verificarDisponibilidad(pistaId: Int, fecha: String, hora: String): Boolean {
        val db = dbHelper.readableDatabase

        // Busca reservas ACTIVAS (estadoId = 1) con esa pista, fecha y hora
        val cursor = db.query(
            DatabaseHelper.TABLE_RESERVA,
            arrayOf("COUNT(*) as total"),  // Cuenta cuántas reservas hay
            "${DatabaseHelper.COLUMN_PISTA_ID} = ? AND " +
                    "${DatabaseHelper.COLUMN_FECHA} = ? AND " +
                    "${DatabaseHelper.COLUMN_HORA} = ? AND " +
                    "${DatabaseHelper.COLUMN_ESTADO_ID} = 1",  // Solo activas
            arrayOf(pistaId.toString(), fecha, hora),
            null, null, null
        )

        var disponible = true
        if (cursor.moveToFirst()) {
            val total = cursor.getInt(cursor.getColumnIndexOrThrow("total"))
            disponible = total == 0  // Si total = 0, está disponible
        }
        cursor.close()
        return disponible
    }

    //  Verificar disponibilidad para editar

    // Igual que la anterior, pero EXCLUYE la reserva actual
    fun verificarDisponibilidadParaEditar(
        reservaId: Int,
        pistaId: Int,
        fecha: String,
        hora: String
    ): Boolean {
        val db = dbHelper.readableDatabase

        // Misma consulta pero excluye el ID de la reserva que estamos editando
        val cursor = db.query(
            DatabaseHelper.TABLE_RESERVA,
            arrayOf("COUNT(*) as total"),
            "${DatabaseHelper.COLUMN_PISTA_ID} = ? AND " +
                    "${DatabaseHelper.COLUMN_FECHA} = ? AND " +
                    "${DatabaseHelper.COLUMN_HORA} = ? AND " +
                    "${DatabaseHelper.COLUMN_ESTADO_ID} = 1 AND " +
                    "${DatabaseHelper.COLUMN_ID} != ?",  // Excluir esta reserva
            arrayOf(pistaId.toString(), fecha, hora, reservaId.toString()),
            null, null, null
        )

        var disponible = true
        if (cursor.moveToFirst()) {
            val total = cursor.getInt(cursor.getColumnIndexOrThrow("total"))
            disponible = total == 0
        }
        cursor.close()
        return disponible
    }


    // Devuelve lista completa de reservas con todos los datos
    // Hace JOIN con cliente, pista y estado para tener la información completa
    fun obtenerTodasLasReservas(): List<Reserva> {
        val reservas = mutableListOf<Reserva>()
        val db = dbHelper.readableDatabase

        // Consulta SQL que JUNTA 4 tablas:
        // reserva + cliente + pista + estado
        val query = """
            SELECT 
                r.${DatabaseHelper.COLUMN_ID} as reservaId,
                r.${DatabaseHelper.COLUMN_FECHA},
                r.${DatabaseHelper.COLUMN_HORA},
                c.${DatabaseHelper.COLUMN_NOMBRE} as clienteNombre,
                c.${DatabaseHelper.COLUMN_APELLIDO} as clienteApellido,
                p.${DatabaseHelper.COLUMN_NOMBRE_PISTA} as pistaNombre,
                e.${DatabaseHelper.COLUMN_NOMBRE_ESTADO} as estadoNombre,
                r.${DatabaseHelper.COLUMN_ESTADO_ID}
            FROM ${DatabaseHelper.TABLE_RESERVA} r
            INNER JOIN ${DatabaseHelper.TABLE_CLIENTE} c ON r.${DatabaseHelper.COLUMN_CLIENTE_ID} = c.${DatabaseHelper.COLUMN_ID}
            INNER JOIN ${DatabaseHelper.TABLE_PISTA} p ON r.${DatabaseHelper.COLUMN_PISTA_ID} = p.${DatabaseHelper.COLUMN_ID}
            INNER JOIN ${DatabaseHelper.TABLE_ESTADO} e ON r.${DatabaseHelper.COLUMN_ESTADO_ID} = e.${DatabaseHelper.COLUMN_ID}
            ORDER BY r.${DatabaseHelper.COLUMN_FECHA} DESC, r.${DatabaseHelper.COLUMN_HORA} DESC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        with(cursor) {
            while (moveToNext()) {
                val reserva = Reserva(
                    id = getInt(getColumnIndexOrThrow("reservaId")),
                    clienteId = 0,  //  ya tenemos nombre
                    pistaId = 0,     // ya tenemos nombre
                    fecha = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_FECHA)),
                    hora = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_HORA)),
                    estadoId = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ESTADO_ID)),
                    clienteNombre = getString(getColumnIndexOrThrow("clienteNombre")),
                    clienteApellido = getString(getColumnIndexOrThrow("clienteApellido")),
                    pistaNombre = getString(getColumnIndexOrThrow("pistaNombre")),
                    estadoNombre = getString(getColumnIndexOrThrow("estadoNombre"))
                )
                reservas.add(reserva)
            }
            close()
        }

        return reservas
    }

    // Busca reservas donde el nombre o apellido del cliente contengan el texto buscado
    fun buscarReservasPorCliente(nombreBusqueda: String): List<Reserva> {
        val reservas = mutableListOf<Reserva>()
        val db = dbHelper.readableDatabase

        // Misma consulta que arriba pero con filtro WHERE
        val query = """
            SELECT 
                r.${DatabaseHelper.COLUMN_ID} as reservaId,
                r.${DatabaseHelper.COLUMN_FECHA},
                r.${DatabaseHelper.COLUMN_HORA},
                c.${DatabaseHelper.COLUMN_NOMBRE} as clienteNombre,
                c.${DatabaseHelper.COLUMN_APELLIDO} as clienteApellido,
                p.${DatabaseHelper.COLUMN_NOMBRE_PISTA} as pistaNombre,
                e.${DatabaseHelper.COLUMN_NOMBRE_ESTADO} as estadoNombre,
                r.${DatabaseHelper.COLUMN_ESTADO_ID}
            FROM ${DatabaseHelper.TABLE_RESERVA} r
            INNER JOIN ${DatabaseHelper.TABLE_CLIENTE} c ON r.${DatabaseHelper.COLUMN_CLIENTE_ID} = c.${DatabaseHelper.COLUMN_ID}
            INNER JOIN ${DatabaseHelper.TABLE_PISTA} p ON r.${DatabaseHelper.COLUMN_PISTA_ID} = p.${DatabaseHelper.COLUMN_ID}
            INNER JOIN ${DatabaseHelper.TABLE_ESTADO} e ON r.${DatabaseHelper.COLUMN_ESTADO_ID} = e.${DatabaseHelper.COLUMN_ID}
            WHERE c.${DatabaseHelper.COLUMN_NOMBRE} LIKE ?    -- Buscar en nombre
               OR c.${DatabaseHelper.COLUMN_APELLIDO} LIKE ?  -- O buscar en apellido
            ORDER BY r.${DatabaseHelper.COLUMN_FECHA} DESC, r.${DatabaseHelper.COLUMN_HORA} DESC
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf("%$nombreBusqueda%", "%$nombreBusqueda%"))

        with(cursor) {
            while (moveToNext()) {
                val reserva = Reserva(
                    id = getInt(getColumnIndexOrThrow("reservaId")),
                    clienteId = 0,
                    pistaId = 0,
                    fecha = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_FECHA)),
                    hora = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_HORA)),
                    estadoId = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ESTADO_ID)),
                    clienteNombre = getString(getColumnIndexOrThrow("clienteNombre")),
                    clienteApellido = getString(getColumnIndexOrThrow("clienteApellido")),
                    pistaNombre = getString(getColumnIndexOrThrow("pistaNombre")),
                    estadoNombre = getString(getColumnIndexOrThrow("estadoNombre"))
                )
                reservas.add(reserva)
            }
            close()
        }

        return reservas
    }

    // Modifica los datos de una reserva existente
    // Devuelve el número de filas afectadas (1 si funcionó, 0 si no)
    fun actualizarReserva(
        reservaId: Int,
        pistaId: Int,
        fecha: String,
        hora: String,
        estadoId: Int
    ): Int {
        val db = dbHelper.writableDatabase

        // Preparamos los nuevos datos
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PISTA_ID, pistaId)
            put(DatabaseHelper.COLUMN_FECHA, fecha)
            put(DatabaseHelper.COLUMN_HORA, hora)
            put(DatabaseHelper.COLUMN_ESTADO_ID, estadoId)
        }

        // Actualizamos donde el ID coincida
        return db.update(
            DatabaseHelper.TABLE_RESERVA,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(reservaId.toString())
        )
    }

    // Borra una reserva de la base de datos
    // Devuelve el número de filas eliminadas (1 si funcionó, 0 si no)
    fun eliminarReserva(reservaId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            DatabaseHelper.TABLE_RESERVA,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(reservaId.toString())
        )
    }

    // Devuelve un mapa con el nombre de cada pista y cuántas reservas activas tiene HOY
    fun obtenerResumenOcupacion(): Map<String, Int> {
        val resumen = mutableMapOf<String, Int>()
        val db = dbHelper.readableDatabase

        // Consulta: por cada pista, cuenta reservas activas de hoy
        val query = """
            SELECT 
                p.${DatabaseHelper.COLUMN_NOMBRE_PISTA} as pista,
                COUNT(r.${DatabaseHelper.COLUMN_ID}) as totalReservas
            FROM ${DatabaseHelper.TABLE_PISTA} p
            LEFT JOIN ${DatabaseHelper.TABLE_RESERVA} r 
                ON p.${DatabaseHelper.COLUMN_ID} = r.${DatabaseHelper.COLUMN_PISTA_ID}
                AND r.${DatabaseHelper.COLUMN_FECHA} = date('now')  -- Solo hoy
                AND r.${DatabaseHelper.COLUMN_ESTADO_ID} = 1        -- Solo activas
            GROUP BY p.${DatabaseHelper.COLUMN_ID}
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        with(cursor) {
            while (moveToNext()) {
                val pista = getString(getColumnIndexOrThrow("pista"))
                val total = getInt(getColumnIndexOrThrow("totalReservas"))
                resumen[pista] = total
            }
            close()
        }

        return resumen
    }
}