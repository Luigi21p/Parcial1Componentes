package com.ud.parcial1componentes.logica

import com.ud.parcial1componentes.persistencia.ClienteDAO

//  buscador que ayuda a encontrar clientes por nombre o ID
class BuscarCliente(private val clienteDAO: ClienteDAO) {

    // Buscar clientes por nombre

    fun porNombre(busqueda: String): List<Cliente> {
        return if (busqueda.isBlank()) {
            // Si no escribió nada, muestra todos
            clienteDAO.obtenerTodosLosClientes()
        } else {
            // Si escribió algo, busca por ese texto
            clienteDAO.buscarClientesPorNombre(busqueda)
        }
    }

    // Obtener todos los clientes
    // Devuelve una lista con TODOS los clientes guardados
    fun todosLosClientes(): List<Cliente> {
        return clienteDAO.obtenerTodosLosClientes()
    }

    // Verificar si un cliente ya existe
    fun existeCliente(nombre: String, apellido: String): Boolean {
        // Busca clientes que tengan ese nombre
        val clientes = clienteDAO.buscarClientesPorNombre(nombre)

        // Revisa si alguno coincide también en apellido
        return clientes.any {
            it.nombre.equals(nombre, ignoreCase = true) &&
                    it.apellido.equals(apellido, ignoreCase = true)
        }
    }

    // Buscar cliente por su ID
    fun obtenerClientePorId(id: Int): Cliente? {
        // Obtiene todos y busca el que tenga ese ID
        return clienteDAO.obtenerTodosLosClientes().find { it.id == id }
    }
}