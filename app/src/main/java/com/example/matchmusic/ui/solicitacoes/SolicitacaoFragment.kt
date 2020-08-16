package com.example.matchmusic.ui.solicitacoes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matchmusic.Adapter.ListAdapter
import com.example.matchmusic.Daos.PedidoMDao
import com.example.matchmusic.Daos.UsuarioMDao
import com.example.matchmusic.Model.Amizade
import com.example.matchmusic.Model.Usuario

import com.example.matchmusic.R
import com.example.matchmusic.ViewModel.UsuarioViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_solicitacao.*
import java.lang.Exception


class SolicitacaoFragment : Fragment() {

    private lateinit var usuarioViewModel: UsuarioViewModel
    private var UsuarioMDao : UsuarioMDao? = null
    private var PedidoMDao : PedidoMDao? = null

    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private var meusAmigos : ArrayList<Usuario> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        UsuarioMDao = UsuarioMDao()
        PedidoMDao = PedidoMDao()

        activity?.let {
            usuarioViewModel = ViewModelProviders.of(it).get(UsuarioViewModel::class.java)
        }

        return inflater.inflate(R.layout.fragment_solicitacao, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listarSolicitacao()

        FAB_procAmigos.setOnClickListener {
            infoBox.visibility = View.VISIBLE
        }

        infoBox.setOnClickListener {
            infoBox.visibility = View.GONE
        }

    }

    private fun listarSolicitacao(){
        PedidoMDao?.pegarPedidosThen { response, msg, result ->
            if (response){
                val amigos: ArrayList<Amizade> = ArrayList()
                result?.documents?.forEach {
                    val amigo = it.toObject(Amizade::class.java)
                    if (!amigo?.confirmacao!!){
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
                            usuarios.add(usuario!!)
                        }

                        amigos.forEach { it1 ->
                            usuarios.forEach { it2 ->
                                if (it1.solicitante == it2.email && it1.solicitante != mAuth.currentUser?.email){
                                    meusAmigos.add(it2)
                                }else if (it1.solicitado == it2.email && it1.solicitado != mAuth.currentUser?.email){
                                    meusAmigos.add(it2)
                                }
                            }
                        }

                        try{
                            var listAdapter = ListAdapter(meusAmigos)
                            RecView.adapter = listAdapter
                            RecView.layoutManager = LinearLayoutManager(context)
                            Toast.makeText(context, "Listando....", Toast.LENGTH_LONG ).show()
                        }catch (e: Exception){}

                        val TouchHelperRight = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                            0, ItemTouchHelper.RIGHT
                        ){
                            override fun onMove(
                                recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder
                            ): Boolean = false
                            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                                val position = viewHolder.adapterPosition
                                aceitarPedido(meusAmigos[position])
                            }
                        })
                        TouchHelperRight.attachToRecyclerView(RecView)

                        val TouchHelperLeft = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                            0, ItemTouchHelper.LEFT
                        ){
                            override fun onMove(
                                recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder
                            ): Boolean = false
                            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                                val position = viewHolder.adapterPosition
                                recusarPedido(meusAmigos[position])
                            }
                        })
                        TouchHelperLeft.attachToRecyclerView(RecView)


                    }
                    Log.d("PEGAR USUARIOS", msg2)
                }
            }
            Log.d("PEGAR PEDIDOS", msg)
        }
    }

    private fun aceitarPedido(usuario: Usuario){
        mAuth.currentUser?.email?.let {
            PedidoMDao?.pegarPedidoPorEmailsThen(usuario.email, it){ response, msg, result ->
                if (response){
                    try {
                        val amizade = result?.documents?.get(0)?.toObject(Amizade::class.java)
                        val idDocumento = result?.documents?.get(0)?.id
                        amizade?.confirmacao = true

                        if (idDocumento != null) {
                            if (amizade != null) {
                                atualizarPedido(idDocumento, amizade)
                            }
                        }

                    }catch (e: Exception){
                        Toast.makeText(context, "Já enviou o pedido de amizade para este usuario.", Toast.LENGTH_LONG).show()
                        meusAmigos.clear()
                        listarSolicitacao()
                    }
                }
                Log.d("PEGAR PEDIDO POR EMAILS", msg)
            }
        }
    }

    private fun atualizarPedido(idDocumento: String, amizade: Amizade){
        PedidoMDao?.atualizarPedidoThen(idDocumento, amizade){ response, msg ->
            if (response){
                Toast.makeText(context, "Pedido aceito!", Toast.LENGTH_LONG).show()
            }
            Log.d("ATUALIZAR O PEDIDO", msg)
        }
    }

    private fun recusarPedido(usuario: Usuario){
        mAuth.currentUser?.email?.let {
            PedidoMDao?.pegarPedidoPorEmailsThen(usuario.email, it){ response, msg, result ->
                if (response){
                    try {
                        val idDocumento = result?.documents?.get(0)?.id
                        if (idDocumento != null) {
                            deletarPedido(idDocumento)
                        }
                    }catch (e: Exception){
                        mAuth.currentUser!!.email?.let { it1 ->
                            PedidoMDao!!.pegarPedidoPorEmailsThen(it1, usuario.email){ response, msg, result ->
                                if (response){
                                    val idDocumento = result?.documents?.get(0)?.id
                                    if (idDocumento != null) {
                                        deletarPedido(idDocumento)
                                    }
                                }
                                Log.d("DELEÇÃO DE SOLICITACAO", msg)
                            }
                        }
                    }
                }
                Log.d("PEGAR PEDIDO POR EMAILS", msg)
            }
        }
    }

    private fun deletarPedido(idDocumento: String){
        PedidoMDao?.deletarPedidoThen(idDocumento){ response, msg ->
            if (response){
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
            Log.d("DELETAR PEDIDO", msg)
        }
    }
}
