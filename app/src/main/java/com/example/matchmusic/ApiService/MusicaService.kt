package com.example.matchmusic.ApiService

import com.example.matchmusic.Model.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MusicaService {

    @GET("artist/{valor}/top?limit=3")
    fun getAll(@Path("valor") valor: Long): Call<TrackResponse>

}