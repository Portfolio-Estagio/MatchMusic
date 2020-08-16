package com.example.matchmusic.throwss

class UsuarioNomeException : Throwable() {
    override val message: String?
        get() = "Nome deve ter mais que dois caracteres."
}
