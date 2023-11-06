package com.javeriana.taller3.model

class Usuario(
    val email: String? = null, val password: String? = null,
    val nombre: String? = null, val apellido: String? = null,
    val numeroId: String? = null, val latitud: Long? = null, val longitud: Long? = null,
    var urlImage: String? = null) {
    constructor(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        numeroId: String,
        urlImage: String
    ) : this(email, password, nombre, apellido, numeroId, 0, 0, urlImage)
}