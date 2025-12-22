package com.squad.musicmatters.core.model


data class SearchHistoryItem(
    val id: String,
    val category: SearchFilter
)

private fun getSearchFilterFrom( name: String ) = when ( name ) {
    SearchFilter.SONG.name -> SearchFilter.SONG
    SearchFilter.ALBUM.name -> SearchFilter.ALBUM
    SearchFilter.ARTIST.name -> SearchFilter.ARTIST
    SearchFilter.GENRE.name -> SearchFilter.GENRE
    else -> SearchFilter.PLAYLIST
}

enum class SearchFilter {
    SONG,
    ALBUM,
    ARTIST,
    GENRE,
    PLAYLIST,
}
