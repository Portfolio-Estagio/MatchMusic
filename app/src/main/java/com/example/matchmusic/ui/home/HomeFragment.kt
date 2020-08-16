package com.example.matchmusic.ui.home

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.matchmusic.Daos.UsuarioMDao
import com.example.matchmusic.LoginActivity
import com.example.matchmusic.Model.Usuario
import com.example.matchmusic.R
import com.example.matchmusic.ViewModel.UsuarioViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var mAdView: AdView
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var usuarioViewModel: UsuarioViewModel
    private var UsuarioMDao : UsuarioMDao? = null

    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    companion object{
        var usuario : Usuario? = null
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        activity?.let {
            usuarioViewModel = ViewModelProviders.of(it).get(UsuarioViewModel::class.java)
            homeViewModel = ViewModelProviders.of(it).get(HomeViewModel::class.java)
        }

        UsuarioMDao = UsuarioMDao()
        usuario = usuarioViewModel.me

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        MobileAds.initialize(context)
        mAdView = root.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        return root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        carregarGeneros()
        txtNome.text = "${usuario?.nome} ${usuario?.sobrenome}"
        txtPensamento.text = usuario?.pensamento
        txtSobremim.text = usuario?.sobremim
        txtSobre.text = "Sobre ${usuario?.nome} ${usuario?.sobrenome}"

        btnEditarPerfil.setOnClickListener {
            if(eTPensamento.visibility == View.VISIBLE && eTSobremim.visibility == View.VISIBLE) {
                eTPensamento.visibility = View.GONE
                eTSobremim.visibility = View.GONE

                usuario?.pensamento = eTPensamento.text.toString()
                usuario?.sobremim = eTSobremim.text.toString()

                usuario?.let { it1 ->
                    UsuarioMDao?.atualizarSobreUsuarioThen(it1) { response, msg ->
                        if (response){
                            Toast.makeText(context, "Atualizou com sucesso!", Toast.LENGTH_LONG).show()
                        }
                        Log.d("ATUALIZAR INFOS", msg)
                    }
                }

                txtPensamento.text = usuario?.pensamento
                txtSobremim.text = usuario?.sobremim

                txtPensamento.visibility = View.VISIBLE
                txtSobremim.visibility = View.VISIBLE
            } else {
                eTPensamento.visibility = View.VISIBLE
                eTSobremim.visibility = View.VISIBLE
                txtPensamento.visibility = View.GONE
                txtSobremim.visibility = View.GONE
            }
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
                    txtGeneros.text = listaGeneros.joinToString(", ")
                }
            }
        }
    }
}
