package com.ud.parcial1componentes.persistencia

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


//  crear y administrar la base de datos
// constructor" que hace las tablas donde guardaremos la información
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // guardar los datos que nunca cambian, como el nombre de la BD y las tablas
    companion object {
        // Información de BD
        private const val DATABASE_NAME = "bowling.db"
        private const val DATABASE_VERSION = 1      //version

        // tablas
        const val TABLE_CLIENTE = "cliente"
        const val TABLE_PISTA = "pista"
        const val TABLE_ESTADO = "estado"
        const val TABLE_RESERVA = "reserva"

        // columna ID
        const val COLUMN_ID = "id"

        // columna cliente
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_APELLIDO = "apellido"

        // columna pista
        const val COLUMN_NOMBRE_PISTA = "nombre"

        // columna estado
        const val COLUMN_NOMBRE_ESTADO = "nombre"

        // columna reserva
        const val COLUMN_CLIENTE_ID = "clienteId"
        const val COLUMN_PISTA_ID = "pistaId"
        const val COLUMN_FECHA = "fecha"
        const val COLUMN_HORA = "hora"
        const val COLUMN_ESTADO_ID = "estadoId"
    }

    // onCreate - se ejecuta la primera vez
    override fun onCreate(db: SQLiteDatabase) {

        // 1. CREAR TABLA CLIENTE
        // Guarda la información de las personas que reservan
        db.execSQL("""
            CREATE TABLE $TABLE_CLIENTE (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,  
                $COLUMN_NOMBRE TEXT NOT NULL,                   
                $COLUMN_APELLIDO TEXT NOT NULL                   
            )
        """)

        // 2. CREAR TABLA PISTA
        // Guarda las pistas de bowling disponibles (1-8)
        db.execSQL("""
            CREATE TABLE $TABLE_PISTA (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE_PISTA TEXT NOT NULL              
            )
        """)

        // 3. CREAR TABLA ESTADO
        // Guarda los posibles estados: Activa, Cancelada, Completada
        db.execSQL("""
            CREATE TABLE $TABLE_ESTADO (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE_ESTADO TEXT NOT NULL             
            )
        """)

        // 4. CREAR TABLA RESERVA
        // Guarda las reservas (relaciona cliente, pista y estado)
        db.execSQL("""
            CREATE TABLE $TABLE_RESERVA (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CLIENTE_ID INTEGER NOT NULL,           
                $COLUMN_PISTA_ID INTEGER NOT NULL,              
                $COLUMN_FECHA TEXT NOT NULL,                    
                $COLUMN_HORA TEXT NOT NULL,                      
                $COLUMN_ESTADO_ID INTEGER NOT NULL,              
                FOREIGN KEY ($COLUMN_CLIENTE_ID) REFERENCES $TABLE_CLIENTE($COLUMN_ID),
                FOREIGN KEY ($COLUMN_PISTA_ID) REFERENCES $TABLE_PISTA($COLUMN_ID),
                FOREIGN KEY ($COLUMN_ESTADO_ID) REFERENCES $TABLE_ESTADO($COLUMN_ID)
            )
        """)

        // Insertar datos (estados y pistas)
        insertarDatosIniciales(db)
    }

    // onUpgrade - cuando actualizas la app
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Borrar tablas si existen
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RESERVA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PISTA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ESTADO")

        // Volver a crear las tablas
        onCreate(db)
    }

    // información que la app necesita desde el principio
    private fun insertarDatosIniciales(db: SQLiteDatabase) {

        //  deben existir para que las reservas tengan estado
        val estados = listOf("Activa", "Cancelada", "Completada")
        estados.forEach { estado ->
            val values = ContentValues().apply {
                put(COLUMN_NOMBRE_ESTADO, estado)
            }
            db.insert(TABLE_ESTADO, null, values)
        }

        // Insertar las 8 pistas
        for (i in 1..8) {
            val values = ContentValues().apply {
                put(COLUMN_NOMBRE_PISTA, "Pista $i")
            }
            db.insert(TABLE_PISTA, null, values)
        }
    }
}