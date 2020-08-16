package com.example.matchmusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import androidx.lifecycle.ViewModelProviders
import com.example.matchmusic.Daos.UsuarioMDao
import com.example.matchmusic.Model.Usuario
import com.example.matchmusic.ViewModel.UsuarioViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_genero.*

class GeneroActivity : AppCompatActivity() {

    lateinit var usuarioViewModel: UsuarioViewModel
    private var UsuarioMDao : UsuarioMDao? = null

    private val mAuth :FirebaseAuth = FirebaseAuth.getInstance()
    /*private var dbFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()*/

    companion object{
        var listaGenero : MutableList<CheckBox>? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_genero)

        usuarioViewModel = ViewModelProviders.of(this).get(UsuarioViewModel::class.java)

        listaGenero = mutableListOf<CheckBox>(
            findViewById(R.id.Rock), findViewById(R.id.Funk),
            findViewById(R.id.Forro), findViewById(R.id.MPB),
            findViewById(R.id.Pop), findViewById(R.id.Eletronica),
            findViewById(R.id.Sertanejo), findViewById(R.id.Samba), findViewById(R.id.Pagode)
            //findViewById(R.id.Gospel), findViewById(R.id.Classico), findViewById(R.id.Outro)
        )

        UsuarioMDao = UsuarioMDao()

        GeneroButton.setOnClickListener {
            var lista = ArrayList<String>()

            mAuth.currentUser?.email?.let { it1 -> UsuarioMDao!!.pegarUsuarioPeloEmailThen(it1){ response, msg, result ->
                if (response){
                    val usuario = result?.toObject(Usuario::class.java)

                    if (usuario != null){
                        listaGenero!!.forEach {
                            if(it.isChecked){
                                lista.add(it.text.toString())
                            }
                        }
                        usuario.generos = lista
                        val intt = Intent(this, MainActivity::class.java)
                        intt.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

                        UsuarioMDao!!.atualizarGeneroUsuarioThen(usuario) { response_2, msg_2 ->
                            if (response_2){
                                intt.putExtra("usuario", usuario)
                                startActivity(intt)
                            }
                            Log.d("ATUALIZAÇÃO USUARIO", msg_2)
                        }
                    }
                }
                Log.d("GENERO MUSICAL", msg)
            } }


            //mAuth.currentUser?.email?.let { it1 -> FBManagerDao!!.pegarUsuarioPeloEmail(it1) }


            /*val userReference = dbFirestore.collection("users").whereEqualTo("email", mAuth.currentUser?.email)
            userReference.get().addOnCompleteListener{
                when{
                    it.isSuccessful ->{
                        var documento = it.result?.documents?.get(0)
                        var usuario = documento?.toObject(Usuario::class.java)!!
                        Log.d("Logged", "Id getUsuario: ${usuario.nome}")

                        if(usuario != null){
                            listaGenero!!.forEach{
                                if(it.isChecked){
                                    lista.add(it.toString())
                                }
                            }
                            usuario.generos = lista
                            val intt = Intent(this, MainActivity::class.java)

                            dbFirestore.collection("users").document(usuario.email).update("generos", usuario.generos)
                            intt.putExtra("usuario", usuario)
                            startActivity(intt)
                        }
                    }
                }
            }*/
        }
   }
}
