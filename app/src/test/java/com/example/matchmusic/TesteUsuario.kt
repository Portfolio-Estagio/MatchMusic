package com.example.matchmusic

import com.example.matchmusic.Model.Usuario
import com.example.matchmusic.throwss.*
import org.junit.Assert
import org.junit.Test

class TesteUsuario {
    lateinit var usuario: Usuario


    @Test
    fun a_verificar_se_e_uma_instancia_de_produto(){
        usuario = Usuario("mario", "silva", "","","mario@email.com", "mario1234")
        Assert.assertTrue(usuario is Usuario)
    }

    @Test
    fun verifica_se_nome_tem_mais_de_dois_caracteres(){
        try{
            var usuario = Usuario( "ma", "silva", "","","mario@email.com", "mario1234")
            junit.framework.Assert.assertTrue(false)
        }catch(e: UsuarioNomeException){
            junit.framework.Assert.assertEquals(
                UsuarioNomeException().message,
                e.message
            )
        }
    }

    @Test
    fun verifica_se_sobrenome_tem_mais_de_dois_caracteres(){
        try{
            var usuario = Usuario("mario", "si","","", "mario@email.com", "mario1234")
            junit.framework.Assert.assertTrue(false)
        }catch(e: UsuarioSobrenomeException){
            junit.framework.Assert.assertEquals(
                UsuarioSobrenomeException().message,
                e.message
            )
        }
    }

    @Test
    fun verifica_se_senha_tem_mais_de_oito_caracteres(){
        try{
            var usuario = Usuario("mario", "silva", "","","mario@email.com", "mario12")
            junit.framework.Assert.assertTrue(false)
        }catch(e: UsuarioSenhaException){
            junit.framework.Assert.assertEquals(
                UsuarioSenhaException().message,
                e.message
            )
        }
    }

    @Test
    fun verifica_se_senha_tem_menos_de_dezesseis_caracteres(){
        try{
            var usuario = Usuario("mario", "silva","","", "mario@email.com", "12345678912345678")
            junit.framework.Assert.assertTrue(false)
        }catch(e: UsuarioSenhaGrandeException){
            junit.framework.Assert.assertEquals(
                UsuarioSenhaGrandeException().message,
                e.message
            )
        }
    }
    @Test
    fun verifica_se_email_e_valido(){
        try{
            var usuario = Usuario("mario", "silva","","", "marioooooooooooooo", "mario1234")
            junit.framework.Assert.assertTrue(false)
        }catch(e: UsuarioEmailException){
            junit.framework.Assert.assertEquals(
                UsuarioEmailException().message,
                e.message
            )
        }
    }
}