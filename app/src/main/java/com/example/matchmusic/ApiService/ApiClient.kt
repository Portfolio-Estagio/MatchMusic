package com.example.matchmusic.ApiService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private var instance : Retrofit? = null
    private val url : String = "https://api.deezer.com/"
    private fun getInstance(): Retrofit {
        if (instance == null){
            instance = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return instance as Retrofit
    }

    fun getArtistaService(): ArtistaService = getInstance().create(ArtistaService::class.java)
    fun getMusicaService(): MusicaService = getInstance().create(MusicaService::class.java)
}