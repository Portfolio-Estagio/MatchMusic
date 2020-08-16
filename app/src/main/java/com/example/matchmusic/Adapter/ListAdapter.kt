package com.example.matchmusic.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.matchmusic.Model.Usuario
import com.example.matchmusic.R
import kotlinx.android.synthetic.main.recycler_view_amigos_itens.view.*

class ListAdapter(
    val usuarios: List<Usuario>
    //val callback: (Usuario) -> Unit
)    : RecyclerView.Adapter<ListAdapter.UsuarioViewHolder>(){

    class UsuarioViewHolder(view : View)
        : RecyclerView.ViewHolder(view) {
        val campoNome = view.nomeCompleto

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : UsuarioViewHolder {
        val v = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.recycler_view_amigos_itens, // Representa um item
                parent,false
            )
        val usuarioViewHolder = UsuarioViewHolder(v)

        /*usuarioViewHolder.itemView.setOnClickListener {
            val usuarioClick = usuarios[usuarioViewHolder.adapterPosition]
            callback(usuarioClick)
        }*/

        return usuarioViewHolder
    }

    override fun getItemCount(): Int = usuarios.size

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]

        holder.campoNome.text = usuario.nomeCompleto()

    }
}