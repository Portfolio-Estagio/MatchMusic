package com.example.matchmusic.throwss

class UsuarioSenhaGrandeException : Throwable() {
    override val message: String?
        get() = "Senha deve ter menos que dezesseis caracteres."
}