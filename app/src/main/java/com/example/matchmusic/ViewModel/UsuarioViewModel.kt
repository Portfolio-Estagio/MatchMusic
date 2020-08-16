package com.example.matchmusic.ViewModel

import androidx.lifecycle.ViewModel
import com.example.matchmusic.Model.Usuario
import com.google.firebase.auth.FirebaseUser

class UsuarioViewModel() : ViewModel() {
    var selected: Usuario? = null
    var me: Usuario? = null
}