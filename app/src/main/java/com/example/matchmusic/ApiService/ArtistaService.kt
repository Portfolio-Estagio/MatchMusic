package com.example.matchmusic.ApiService

import com.example.matchmusic.Model.Artist
import com.example.matchmusic.Model.Genre
import retrofit2.http.GET
import retrofit2.Call

interface ArtistaService {

    @GET("genre/152/artists")
    fun getAllRock(): Call<Genre>

    @GET("genre/472/artists")
    fun getlAllFunk(): Call<Genre>

    @GET("genre/132/artists")
    fun getAllPop(): Call<Genre>

    @GET("genre/80/artists")
    fun getAllSertanejo(): Call<Genre>

    @GET("genre/79/artists")
    fun getAllsSambaPagode(): Call<Genre>

    @GET("genre/106/artists")
    fun getAllEletronic(): Call<Genre>

    @GET("genre/78/artists")
    fun getAllMpb(): Call<Genre>

    @GET("genre/76/artists")
    fun getAllForro(): Call<Genre>
}