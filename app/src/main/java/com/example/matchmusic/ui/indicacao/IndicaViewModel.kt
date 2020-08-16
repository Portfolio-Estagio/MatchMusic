package com.example.matchmusic.ui.indicacao

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.matchmusic.Model.ArtistAndMusics

class IndicaViewModel() : ViewModel() {
    val songLiveData = MutableLiveData<List<ArtistAndMusics>>()
}