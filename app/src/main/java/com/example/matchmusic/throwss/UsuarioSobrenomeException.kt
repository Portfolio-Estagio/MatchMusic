package com.example.matchmusic.throwss

class UsuarioSobrenomeException : Throwable() {
    override val message: String?
        get() = "Sobrenome deve ter mais que dois caracteres."
}
