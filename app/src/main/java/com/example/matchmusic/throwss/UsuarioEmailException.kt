package com.example.matchmusic.throwss

class UsuarioEmailException : Throwable() {
    override val message: String?
        get() = "O email não é valido"
}
