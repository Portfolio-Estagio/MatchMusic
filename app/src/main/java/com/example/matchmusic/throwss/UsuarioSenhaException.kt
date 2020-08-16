package com.example.matchmusic.throwss

class UsuarioSenhaException : Throwable() {
    override val message: String?
        get() = "Senha deve ter mais que oito caracteres."
}