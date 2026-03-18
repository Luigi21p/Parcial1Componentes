package com.ud.parcial1componentes.persistencia

import android.content.ContentValues
import com.ud.parcial1componentes.logica.Cliente


//  las operaciones con clientes en la base de datos
// se guardan, buscan y obtienen los clientes

class ClienteDAO(private val dbHelper: DatabaseHelper) {

    // Insertar cliente


    // recibe nombre y apellido, y devuelve el ID del cliente
    // si existe, devuelve su ID
    // Si el cliente NO existe, lo crea y devuelve su nuevo ID
    fun insertarObtenerCliente(nombre: String, apellido: String): Int {
        val db = dbHelper.writableDatabase  // Abre la BD para escribir

        // Buscar si el cliente ya existe
        val cursor = db.query(
            DatabaseHelper.TABLE_CLIENTE,
            arrayOf(DatabaseHelper.COLUMN_ID),
            "${DatabaseHelper.COLUMN_NOMBRE} = ? AND ${DatabaseHelper.COLUMN_APELLIDO} = ?", // nombre y apellido
            arrayOf(nombre, apellido),   // Valores
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            // El cliente ya existe
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
            cursor.close()  // cerrar el cursor
            id  // Devolver el ID que ya tenía
        } else {
            // El cliente no existe, hay que crearlo
            cursor.close()

            // datos para insertar
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_NOMBRE, nombre)
                put(DatabaseHelper.COLUMN_APELLIDO, apellido)
            }

            // Insertar en la BD
            val newId = db.insert(DatabaseHelper.TABLE_CLIENTE, null, values)
            newId.toInt()  // Devolvemos el nuevo ID
        }
    }

    // Obtener todos los clientes
    // Ordenados alfabéticamente por nombre (A-Z)
    fun obtenerTodosLosClientes(): List<Cliente> {
        val clientes = mutableListOf<Cliente>()  // Lista donde guardaremos los resultados
        val db = dbHelper.readableDatabase       // Abre la BD para leer

        // Consulta
        val cursor = db.query(
            DatabaseHelper.TABLE_CLIENTE,
            arrayOf(
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_NOMBRE,
                DatabaseHelper.COLUMN_APELLIDO
            ),
            null, null, null, null,
            "${DatabaseHelper.COLUMN_NOMBRE} ASC"  // Ordenar por nombre A-Z
        )

        // Recorremos todos los resultados
        with(cursor) {
            while (moveToNext()) {
                // Por cada cliente, creamos un objeto Cliente
                val cliente = Cliente(
                    id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    nombre = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOMBRE)),
                    apellido = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_APELLIDO))
                )
                clientes.add(cliente)  // agrega a la lista
            }
            close()  // Cerrar  el cursor
        }

        return clientes  // Devolver la lista completa
    }

    // Buscar clientes por nombre
    fun buscarClientesPorNombre(busqueda: String): List<Cliente> {
        val clientes = mutableListOf<Cliente>()  // Lista para guardar resultados
        val db = dbHelper.readableDatabase       // Abre la BD para leer

        // Consulta SQL directa
        val query = """
            SELECT * FROM ${DatabaseHelper.TABLE_CLIENTE}
            WHERE ${DatabaseHelper.COLUMN_NOMBRE} LIKE ?    -- Buscar en nombre
               OR ${DatabaseHelper.COLUMN_APELLIDO} LIKE ?   -- O buscar en apellido
            ORDER BY ${DatabaseHelper.COLUMN_NOMBRE} ASC
        """.trimIndent()

        // %busqueda%  cualquier texto que CONTENGA la palabra buscada
        val cursor = db.rawQuery(query, arrayOf("%$busqueda%", "%$busqueda%"))

        // Recorremos los resultados igual que en la función anterior
        with(cursor) {
            while (moveToNext()) {
                val cliente = Cliente(
                    id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    nombre = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOMBRE)),
                    apellido = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_APELLIDO))
                )
                clientes.add(cliente)
            }
            close()
        }

        return clientes  // Devolvemos los clientes encontrados
    }
}