package com.example.matchmusic.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.matchmusic.Model.ArtistAndMusics
import com.example.matchmusic.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.ryview_indica_itens.view.*


class MusicListAdapter(
    val musicas: List<ArtistAndMusics>
): RecyclerView.Adapter<MusicListAdapter.MusicListViewHolder>() {

    class MusicListViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val campoBitmapImage = view.txtArtistaImg
        val campoArtista = view.txtNomeMusica
        val campoMusica = view.txtNomeArtista
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicListViewHolder {
        val v = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.ryview_indica_itens, // Representa um item
                parent,false
            )
        return MusicListViewHolder(v)
    }

    override fun getItemCount(): Int = musicas.size

    override fun onBindViewHolder(holder: MusicListViewHolder, position: Int) {
        val musicas = musicas[position]

        Picasso.with(holder.campoArtista.context)
            .load(musicas.imagem_artista)
            .into(holder.campoBitmapImage, object : com.squareup.picasso.Callback{
                override fun onSuccess() {
                    Log.d("SUCESSO", "Sucesso garai, é trollage n")
                }

                override fun onError() {
                    Log.d("ERRO", "Não foi possivel carregar a imagem.")
                }

            })


        holder.campoArtista.text = "${musicas.nome_musica}"
        holder.campoMusica.text = "Por: ${musicas.nome_artista}"
    }
}