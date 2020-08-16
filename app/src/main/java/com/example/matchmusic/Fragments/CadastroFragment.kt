package com.example.matchmusic.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.matchmusic.Daos.UsuarioMDao
import com.example.matchmusic.Model.EmailValidator
import com.example.matchmusic.Model.Usuario

import com.example.matchmusic.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_cadastro.*

class CadastroFragment : Fragment() {

    private var UsuarioMDao : UsuarioMDao? = null
    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    //private val dbFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        UsuarioMDao = UsuarioMDao()

        return inflater.inflate(R.layout.fragment_cadastro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enviarButton.setOnClickListener {
            val nome :String? = nomeText.text.toString()
            val sobrenome :String?  = sobrenomeText.text.toString()
            val email :String?  = emailText.text.toString()
            val senha :String?  = senhaText.text.toString()

            if(!nome?.isEmpty()!! && !nome.isBlank() && !sobrenome?.isEmpty()!! && !sobrenome.isBlank() && !email?.isEmpty()!! && !email.isBlank()
                && !email.isEmpty() && !email.isBlank()
            ) {
                if(nome.length >= 3 && sobrenome.length >= 3){
                    if(EmailValidator.validarEmail(email)){
                        if (senha != null) {
                            if (senha.length >= 8 && senha.length <= 16) {

                                val listaGeneros = ArrayList<String>()
                                val listaAmigos = ArrayList<Usuario>()

                                val usuario = Usuario(nome, sobrenome, email, senha, "Meu pensamento.", "Sobre mim.",  listaGeneros, listaAmigos)
                                registrarUsuario(usuario)
                                findNavController().navigate(R.id.loginFragment)

                            } else {
                                Toast.makeText(activity,
                                    "Sua senha deve ter entre 8 e 16 caracteres!",
                                    Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    } else {
                        Toast.makeText(activity,
                            "Insira um email valido!!",
                            Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    Toast.makeText(activity,
                        "Nome e sobrenome devem ter mais que três caracteres",
                        Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(activity,
                    "Dados inválidos!!",
                    Toast.LENGTH_LONG)
                    .show()
            }

        }

        cancelarCadastroButton.setOnClickListener{
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun registrarUsuario(usuario: Usuario) {
        mAuth.createUserWithEmailAndPassword(usuario.email, usuario.senha)
            .addOnSuccessListener {
                if (it != null){
                    val usuarioFB = it.user
                    if (usuarioFB != null) {
                        Log.d("Auth", "Usuario: ${usuarioFB.uid} ${usuarioFB.email}")

                        UsuarioMDao?.inserirUsuarioThen(usuario) { response, msg ->
                            if (response){
                                findNavController().navigate(R.id.loginFragment)
                            }
                            Log.d("REGISTRAR STATUS", msg)
                        }
                    }
                }else{
                    Log.d("Auth", "Registrado")
                }
            }
            .addOnFailureListener {
                Log.d("Auth", it.message.toString())
            }
    }

    /*private fun inserirUsuario(usuario: Usuario) {
        val userReference = dbFirestore.collection("users")
        userReference.document().set(usuario).addOnCompleteListener {
            when{
                it.isSuccessful -> {
                    Toast.makeText(context,"Usuario registrado!!", Toast.LENGTH_LONG ).show()
                    findNavController().navigate(R.id.loginFragment)
                }
                else -> {
                    Toast.makeText(context, "Não foi possivel inserir o usuario.", Toast.LENGTH_LONG ).show()
                }
            }
        }
    }*/
}
