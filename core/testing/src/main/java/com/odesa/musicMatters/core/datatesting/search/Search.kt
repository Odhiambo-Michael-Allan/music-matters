package com.odesa.musicMatters.core.datatesting.search

import com.odesa.musicMatters.core.datatesting.albums.testAlbums
import com.odesa.musicMatters.core.datatesting.artists.testArtists
import com.odesa.musicMatters.core.datatesting.genres.testGenres
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistInfos
import com.odesa.musicMatters.core.model.SearchFilter
import com.odesa.musicMatters.core.model.SearchHistoryItem
import java.util.UUID

val testSearchHistoryItems = listOf(
    SearchHistoryItem(
        id = UUID.randomUUID().toString(),
        category = SearchFilter.SONG
    ),
    SearchHistoryItem(
        id = testAlbums.first().title,
        category = SearchFilter.ALBUM
    ),
    SearchHistoryItem(
        id = testArtists.first().name,
        category = SearchFilter.ARTIST
    ),
    SearchHistoryItem(
        id = testGenres.first().name,
        category = SearchFilter.GENRE
    ),
    SearchHistoryItem(
        id = testPlaylistInfos.first().id,
        category = SearchFilter.PLAYLIST
    )
)