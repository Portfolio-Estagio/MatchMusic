package com.example.matchmusic.Model

class Artist(
    var data: List<Track>
) {
    var id: Long? = null
    var name: String? = null
    var picture_medium: String? = null
    var radio: Boolean? = null
    var tracklist: String? = null
    var type: String? = null
}