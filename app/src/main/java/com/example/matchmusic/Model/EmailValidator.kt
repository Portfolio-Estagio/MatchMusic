package com.example.matchmusic.Model

class EmailValidator {
    companion object {
        fun validarEmail(mail: String): Boolean {
            return mail.contains("@") && mail.contains(".")
        }
    }
}