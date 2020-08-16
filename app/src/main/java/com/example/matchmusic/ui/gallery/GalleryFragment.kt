package com.example.matchmusic.ui.gallery

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matchmusic.Adapter.ListAdapter
import com.example.matchmusic.Daos.PedidoMDao
import com.example.matchmusic.Daos.UsuarioMDao
import com.example.matchmusic.LoginActivity
import com.example.matchmusic.Model.Amizade
import com.example.matchmusic.Model.Usuario
import com.example.matchmusic.R
import com.example.matchmusic.ViewModel.UsuarioViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.lang.Exception


class GalleryFragment : Fragment() {

    private var UsuarioMDao : UsuarioMDao? = null
    private var PedidoMDao : PedidoMDao? = null
    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var usuarioViewModel: UsuarioViewModel

    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    private var resultadoPesquisa = ArrayList<Usuario>()
    private var meusAmigos : ArrayList<Usuario> = ArrayList()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        UsuarioMDao = UsuarioMDao()
        PedidoMDao = PedidoMDao()

        activity?.let {
            galleryViewModel = ViewModelProviders.of(it).get(GalleryViewModel::class.java)
            usuarioViewModel = ViewModelProviders.of(it).get(UsuarioViewModel::class.java)
        }

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        FAB_procAmigos.setOnClickListener {
            infoBox.visibility = View.VISIBLE
        }

        infoBox.setOnClickListener {
            infoBox.visibility = View.GONE
        }

        pegarAmigos()

        btnPesquisarNovoAmigo.setOnClickListener {
            if (!pesquisarNAmigoText.text.isNullOrBlank()){
                if (!pesquisarNAmigoText.text.isNullOrEmpty()){
                    val nomePesquisa = pesquisarNAmigoText.text.toString()
                    meusAmigos.clear()
                    encontrarUsuario(nomePesquisa)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_desconectar -> {
                mAuth.signOut()
                val intt = Intent(context, LoginActivity::class.java)
                intt.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intt)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun pegarAmigos(){
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
                        result2?.documents?.forEach {
                            val usuario = it.toObject(Usuario::class.java)
                            if (usuario != null) {
                                usuarios.add(usuario)
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
                        if (resultadoPesquisa.size > 0){
                            resultadoPesquisa.clear()
                        }

                        var listAdapter = ListAdapter(meusAmigos)
                        recView.adapter = listAdapter
                        recView.layoutManager = LinearLayoutManager(context)

                        val touchHelperRight = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                            0, ItemTouchHelper.RIGHT
                        ){
                            override fun onMove(
                                recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder
                            ): Boolean = false
                            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                                val position = viewHolder.adapterPosition

                                try {
                                    usuarioViewModel.selected = meusAmigos[position]
                                }catch (e: Exception){
                                    usuarioViewModel.selected = resultadoPesquisa[position]
                                }
                                findNavController().navigate(R.id.perfilUFragment)
                            }
                        })
                        touchHelperRight.attachToRecyclerView(recView)


                    }
                    Log.d("CONSULTA USUARIOS", msg2)
                }

            }
            Log.d("CONSULTA AMIZADE", msg)
        }
    }

    private fun encontrarUsuario(nome: String) {
        UsuarioMDao?.pegarUsuariosThen { response, msg, result ->
            if (response){
                result?.documents?.forEach {
                    val usuario = it.toObject(Usuario::class.java)
                    if (usuario != null){
                        if (usuario.nomeCompleto().contains(nome.toUpperCase())){
                            if(mAuth.currentUser?.email != usuario.email){
                                resultadoPesquisa.add(usuario)
                            }
                        }
                    }
                }

                var listaSolicitacao = ArrayList<Amizade>()
                PedidoMDao?.pegarPedidosThen { resp, _, rest ->
                    if(resp){
                        rest?.documents?.forEach{ amigo ->
                            val amizade = amigo.toObject(Amizade::class.java)!!
                            if(amizade.solicitante == mAuth.currentUser?.email || amizade.solicitado == mAuth.currentUser?.email){
                                listaSolicitacao.add(amizade)
                            }
                        }
                    }
                    resultadoPesquisa.forEach { p->
                        listaSolicitacao.forEach{ a ->
                            if(p.email == a.solicitado || p.email == a.solicitante){
                                resultadoPesquisa.remove(p)
                            }
                        }
                    }
                    var listAdapter = ListAdapter(resultadoPesquisa)
                    recView.adapter = listAdapter
                    recView.layoutManager = LinearLayoutManager(context)
                }
            }
            Log.d("PESQUISAR POR NOME:", msg)
        }
    }
}
