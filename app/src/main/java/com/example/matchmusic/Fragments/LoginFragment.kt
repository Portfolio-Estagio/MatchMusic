package com.example.matchmusic.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.matchmusic.Daos.UsuarioMDao
import com.example.matchmusic.GeneroActivity
import com.example.matchmusic.MainActivity
import com.example.matchmusic.Model.Usuario
import com.example.matchmusic.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {

    private var UsuarioMDao : UsuarioMDao? = null
    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        UsuarioMDao = UsuarioMDao()

        //mAuth.signOut()
        /*val img: ImageView = loadingLogo as ImageView

        val aniRotate: Animation =
            AnimationUtils.loadAnimation(this.context, R.anim.rotate_clockwise)
        img.startAnimation(aniRotate)*/

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(mAuth.currentUser == null){
            criarConta.setOnClickListener {
                findNavController().navigate(R.id.cadastroFragment)
            }

            loginButton.setOnClickListener {
                val email = emailLogin.text.toString()
                val senha = senhaLogin.text.toString()
                if(email.isNotBlank() && email.isNotEmpty() && email.isNotBlank() && email.isNotEmpty()){
                    logarUsuario(email, senha)
                } else {
                    Toast.makeText(activity, "Campo em branco !!", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            usuarioJaLogado()
        }
    }

    private fun logarUsuario(email: String, senha: String) {
        loadingLogoArea.setVisibility(View.VISIBLE)
        mAuth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener {
                if (it != null){
                    val usuarioFB = it.user
                    if (usuarioFB != null) {
                        Log.d("Auth", "Usuario: ${usuarioFB.uid} ${usuarioFB.email}!")

                        UsuarioMDao?.pegarUsuarioPeloEmailThen(email) { response, msg, result ->
                            if(response){
                                val usuario = result?.toObject(Usuario::class.java)
                                if (usuario != null){
                                    if (usuario.generos.isEmpty()){
                                        startActivity(Intent(activity, GeneroActivity::class.java))
                                    }else{
                                        val intt = Intent(activity, MainActivity::class.java)
                                        intt.putExtra("usuario", usuario)
                                        startActivity(intt)
                                    }
                                }
                            }
                            Log.d("LOGAR STATUS", msg)
                        }
                    }
                } else {
                    Log.d("Auth", "Negado!")
                    loadingLogoArea.setVisibility(View.GONE)
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity,"E-mail ou senha inválidos.", Toast.LENGTH_LONG).show()
                Log.d("Auth", it.message.toString())
                loadingLogoArea.setVisibility(View.GONE)
            }
    }
    private fun usuarioJaLogado(){
        loadingLogoArea.setVisibility(View.VISIBLE)
        mAuth.currentUser?.email?.let {
            UsuarioMDao?.pegarUsuarioPeloEmailThen(it) { response, msg, result ->
                if (response){
                   val usuario = result?.toObject(Usuario::class.java)
                    if (usuario != null) {
                        if (usuario.generos.isEmpty()){
                            startActivity(Intent(activity, GeneroActivity::class.java))
                        }else{
                            val intt = Intent(activity, MainActivity::class.java)
                            intt.putExtra("usuario", usuario)
                            startActivity(intt)
                        }
                    }
                }
                Log.d("SE ESTA LOGADO", msg)
            }
        }

        /*val userReference = firestore.collection("users").whereEqualTo("email", mAuth.currentUser?.email)
        userReference.get().addOnCompleteListener{
            when{
                it.isSuccessful ->{
                    val documento = it.result?.documents?.get(0)
                    val user = documento?.toObject(Usuario::class.java)
                    if(user != null){
                        val intt = Intent(activity, MainActivity::class.java)
                        intt.putExtra("usuario", user)
                        startActivity(intt)
                    }
                }
            }
        }*/
    }
}
