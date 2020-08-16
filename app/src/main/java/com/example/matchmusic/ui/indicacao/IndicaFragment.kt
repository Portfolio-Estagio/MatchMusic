package com.example.matchmusic.ui.indicacao

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.matchmusic.Adapter.MusicListAdapter
import com.example.matchmusic.ApiService.ApiClient
import com.example.matchmusic.Model.*
import com.example.matchmusic.R
import com.example.matchmusic.ViewModel.UsuarioViewModel
import kotlinx.android.synthetic.main.fragment_indica.*
import retrofit2.*
import kotlin.random.Random

class IndicaFragment : Fragment() {
    private lateinit var usuarioViewModel: UsuarioViewModel
    private lateinit var indicaViewModel: IndicaViewModel
    private var musicas = mutableListOf<ArtistAndMusics>()
    private var callList = mutableListOf<Call<Genre>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.let {
            usuarioViewModel = ViewModelProviders.of(it).get(UsuarioViewModel::class.java)
            indicaViewModel = ViewModelProviders.of(it).get(IndicaViewModel::class.java)
        }

        return inflater.inflate(R.layout.fragment_indica, container, false)
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(context, "Buscando musicas, aguarde...", Toast.LENGTH_LONG).show()
        getIndicacao()
    }

    private fun generateRandom(x: Int): Int{
        return Random.nextInt(0, x)
    }

    @ExperimentalStdlibApi
    private fun getIndicacao(){
        val generos = usuarioViewModel.me?.generos

        generos?.forEach{
            when(it){
                "Rock" ->{
                    callList.add(ApiClient.getArtistaService().getAllRock())
                }
                "MPB" ->{
                    callList.add(ApiClient.getArtistaService().getAllMpb())
                }
                "Funk" ->{
                    callList.add(ApiClient.getArtistaService().getlAllFunk())
                }
                "Pop" ->{
                    callList.add(ApiClient.getArtistaService().getAllPop())
                }
                "Eletrônica" ->{
                    callList.add(ApiClient.getArtistaService().getAllEletronic())
                }
                "Forró" ->{
                    callList.add(ApiClient.getArtistaService().getAllForro())
                }
                "Pagode" ->{
                    callList.add(ApiClient.getArtistaService().getAllsSambaPagode())
                }
                "Sertanejo" ->{
                    callList.add(ApiClient.getArtistaService().getAllSertanejo())
                }
            }
        }

        callList[generateRandom(2)].enqueue(
            object : Callback<Genre> {
                override fun onFailure(
                    call: Call<Genre>,
                    t: Throwable) {
                    Log.d("Retrofit ERROR", t.message.toString())
                }

                override fun onResponse(
                    call: Call<Genre>,
                    response: Response<Genre>){
                    val genres = response.body()

                    var listaGenres = genres?.data
                    var artistas = mutableListOf<Artist>()
                    var callListMusic = mutableListOf<Call<TrackResponse>>()

                    for (i in 1..50){
                        val artista = listaGenres?.get(generateRandom(listaGenres.size - 1))
                        var callMusic = ApiClient.getMusicaService().getAll(artista?.id!!)
                        artistas.add(artista)
                        callListMusic.add(callMusic)
                    }

                    requestMusics(artistas, callListMusic)

                    var requestsFeitos = 0
                    indicaViewModel.songLiveData.observe(viewLifecycleOwner, Observer {
                        it.forEach {
                            musicas.add(it)
                        }

                        if (requestsFeitos == callListMusic.size - 1){
                            val listAdapter = MusicListAdapter(musicas.shuffled())
                            rcyRecomendacoes.adapter = listAdapter
                            rcyRecomendacoes.layoutManager = LinearLayoutManager(context)
                            Toast.makeText(context, "Algumas musicas que você pode gostar.", Toast.LENGTH_LONG).show()
                        }
                        requestsFeitos += 1
                    })
                }
            })
    }

    @ExperimentalStdlibApi
    private fun requestMusics(
        artistas: MutableList<Artist>,
        callMusic: MutableList<Call<TrackResponse>>
    ) {
        callMusic.forEach {
            it.enqueue(
                object : Callback<TrackResponse> {
                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        //Log.d("Retrofit ERROR", t.message.toString())
                        Toast.makeText(context, "Não foi possivel encontrar as musicas, verifique sua conexão com a internet", Toast.LENGTH_LONG).show()
                    }
                    override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                        val musicas = response.body()

                        var musicasV2 = mutableListOf<ArtistAndMusics>()
                        val musicasData = musicas?.data

                        musicasData?.forEach { track ->
                            if (artistas[0].id == track.artist?.id){
                                val artistAndMusics = ArtistAndMusics(artistas[0].picture_medium, track.artist?.name, track.title)
                                musicasV2.add(artistAndMusics)
                            }
                        }
                        artistas.removeFirst()
                        indicaViewModel.songLiveData.value = musicasV2
                    }
                })
        }
    }
}