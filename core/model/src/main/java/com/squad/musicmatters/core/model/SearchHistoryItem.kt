package com.squad.musicmatters.core.model


data class SearchHistoryItem(
    val id: String,
    val category: SearchFilter
)

private fun getSearchFilterFrom( name: String ) = when ( name ) {
    SearchFilter.SONGS.name -> SearchFilter.SONGS
    SearchFilter.ALBUMS.name -> SearchFilter.ALBUMS
    SearchFilter.ARTISTS.name -> SearchFilter.ARTISTS
    SearchFilter.GENRES.name -> SearchFilter.GENRES
    else -> SearchFilter.PLAYLISTS
}

enum class SearchFilter {
    ALL,
    SONGS,
    ALBUMS,
    ARTISTS,
    GENRES,
    PLAYLISTS,
}
