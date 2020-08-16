package com.example.matchmusic.ui.perfilu

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.matchmusic.Daos.PedidoMDao
import com.example.matchmusic.Daos.UsuarioMDao
import com.example.matchmusic.Model.Amizade
import com.example.matchmusic.Model.Usuario

import com.example.matchmusic.R
import com.example.matchmusic.ViewModel.UsuarioViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_perfil_u.*
import java.lang.Exception

class PerfilUFragment : Fragment() {

    private lateinit var mAdView: AdView
    private lateinit var usuarioViewModel: UsuarioViewModel
    private var UsuarioMDao : UsuarioMDao? = null
    private var PedidoMDao : PedidoMDao? = null

    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    companion object{
        var usuario : Usuario? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.let {
            usuarioViewModel = ViewModelProviders.of(it).get(UsuarioViewModel::class.java)
        }

        UsuarioMDao = UsuarioMDao()
        PedidoMDao = PedidoMDao()

        usuario = usuarioViewModel.selected

        val root = inflater.inflate(R.layout.fragment_perfil_u, container, false)

        MobileAds.initialize(context)
        mAdView = root.findViewById(R.id.adView2)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        return root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carregarGeneros()
        txtPUNome.text = "${usuario?.nome} ${usuario?.sobrenome}"
        txtPUPensamento.text = usuario?.pensamento
        txtPUSobremim.text = usuario?.sobremim
        txtPUSobre.text = "Sobre ${usuario?.nome} ${usuario?.sobrenome}"
        verificarAmizade()
    }

    private fun verificarAmizade() {
        PedidoMDao?.pegarPedidosThen { response, msg, result ->
            if (response){
                val amigos: ArrayList<Amizade> = ArrayList()
                result?.documents?.forEach {
                    val amigo = it.toObject(Amizade::class.java)
                    if (amigo?.confirmacao!!){
                        if (amigo.solicitado == mAuth.currentUser?.email || amigo.solicitante == mAuth.currentUser?.email){
                            amigos.add(amigo)
                        }
                    }
                }

                var usuarios : ArrayList<Usuario> = ArrayList()

                UsuarioMDao?.pegarUsuariosThen { response2, msg2, result2 ->
                    if (response2){
                        val meusAmigos = arrayListOf<Usuario>()

                        result2?.documents?.forEach {
                            val localusuario = it.toObject(Usuario::class.java)
                            if (localusuario != null) {
                                usuarios.add(localusuario)
                            }
                        }
                        amigos.forEach { it1 ->
                            usuarios.forEach { it2 ->
                                if (it1.solicitante == it2.email && it1.solicitante != mAuth.currentUser?.email){
                                    meusAmigos.add(it2)
                                } else if (it1.solicitado == it2.email && it1.solicitado != mAuth.currentUser?.email){
                                    meusAmigos.add(it2)
                                }
                            }
                        }

                        var found = false
                        for (lUsuario in meusAmigos) {
                            if(lUsuario.email == usuario?.email){
                                found = true
                                break
                            }
                        }

                        if (found){
                            btnAdicionar.visibility = View.GONE
                            btnDesfazerAmizade.visibility = View.VISIBLE

                            btnDesfazerAmizade.setOnClickListener {
                                usuario?.let { it1 -> desfazerAmizade(it1) }
                                btnDesfazerAmizade.visibility = View.GONE
                            }

                        }else{
                            btnAdicionar.visibility = View.VISIBLE
                            btnDesfazerAmizade.visibility = View.GONE

                            btnAdicionar.setOnClickListener {
                                usuario?.let { it1 -> adicionarAmigo(it1) }
                                btnAdicionar.visibility = View.GONE
                            }
                        }
                    }
                    Log.d("CONSULTA USUARIOS", msg2)
                }

            }
            Log.d("CONSULTA AMIZADE", msg)
        }
    }

    private fun carregarGeneros(){
        mAuth.currentUser?.email?.let {
            UsuarioMDao?.pegarUsuarioPeloEmailThen(it){ response, _, result ->
                if (response){
                    var listaGeneros : ArrayList<String> = ArrayList()
                    val usuario = result?.toObject(Usuario::class.java)
                    usuario?.generos?.forEach {
                        listaGeneros.add(it)
                    }
                    txtPUGeneros.text = listaGeneros.joinToString(", ")
                }
            }
        }
    }

    private fun desfazerAmizade(usuario: Usuario){
        mAuth.currentUser?.email?.let {
            PedidoMDao?.pegarPedidoPorEmailsThen(usuario.email, it) { response, msg, result ->
                if (response){
                    try {
                        val idDocumento = result?.documents?.get(0)?.id

                        idDocumento?.let { it1 -> PedidoMDao!!.deletarPedidoThen(it1){ response, msg ->
                            if (response){
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                            Log.d("DELEÇÃO DE AMIZADE", msg)
                        }}
                    }catch (e: Exception){
                        PedidoMDao!!.pegarPedidoPorEmailsThen(mAuth.currentUser?.email!!, usuario.email){ response2, msg2, result2 ->
                            if (response2){
                                val idDocumento = result2?.documents?.get(0)?.id

                                idDocumento?.let { it1 -> PedidoMDao!!.deletarPedidoThen(it1){ response3, msg3 ->
                                    if (response3){
                                        Toast.makeText(context, msg3, Toast.LENGTH_LONG).show()
                                    }
                                    Log.d("DELEÇÃO DE AMIZADE", msg3)
                                }}
                            }
                            Log.d("DELEÇÃO DE AMIZADE", msg2)
                        }
                    }

                }
                Log.d("BUSCA PESSOA POR EMAIL", msg)
            }
        }
    }

    private fun adicionarAmigo(usuario: Usuario){
        val emailSolicitado = usuario.email
        val emailSolicitante = mAuth.currentUser?.email

        if(emailSolicitante != null){
            val amizade = Amizade()
            amizade.solicitado = emailSolicitado
            amizade.solicitante = emailSolicitante
            amizade.confirmacao = false

            PedidoMDao?.inserirPedidoThen(amizade) { response, msg ->
                if (response){
                    Toast.makeText(context, "Adicionou com sucesso!", Toast.LENGTH_LONG).show()
                }
                Log.d("INSERÇÃO DE AMIZADE", msg)
            }
        }
    }
}
