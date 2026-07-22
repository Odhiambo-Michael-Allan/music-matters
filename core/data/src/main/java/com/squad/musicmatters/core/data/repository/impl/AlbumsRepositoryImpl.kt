package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.AlbumsRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.utils.sortAlbums
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortAlbumsBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.firstOrNull

class AlbumsRepositoryImpl @Inject constructor(
    private val songsRepository: SongsRepository
): AlbumsRepository {

    override fun fetchAlbums(
        sortAlbumsBy: SortAlbumsBy?,
        sortAlbumsInReverse: Boolean
    ): Flow<List<Album>> = songsRepository.fetchSongs()
        .map { songs ->
            fetchAlbumsFrom(
                songs = songs,
                sortAlbumsBy = sortAlbumsBy ?: DefaultPreferences.SORT_ALBUMS_BY,
                sortAlbumsInReverse = sortAlbumsInReverse
            )
        }

    override fun fetchAlbumWithId( id: Long ): Flow<Album> = songsRepository.fetchSongs()
        .map { songs ->
            val songsInAlbum = songs.filter { it.albumId == id }
            val albumTitle = songsInAlbum
                .firstOrNull { !it.albumTitle.isNullOrBlank() }?.albumTitle ?: ""
            Album(
                id = id,
                title = albumTitle,
                trackCount = songsInAlbum.size,
                artworkUri = songsInAlbum.firstOrNull { it.artworkUri != null }?.artworkUri,
                artist = songsInAlbum.firstOrNull { it.albumArtist != null }?.albumArtist,
                artistId = songsInAlbum.first().artistId
            )
        }

    override fun searchAlbumsMatching(
        query: String,
        sortAlbumsBy: SortAlbumsBy,
        sortAlbumsInReverse: Boolean
    ): Flow<List<Album>> = if ( query.isBlank() ) {
        flowOf( emptyList() )
    } else {
        songsRepository.searchSongsInAlbumMatching( query )
            .map { songs ->
                fetchAlbumsFrom(
                    songs = songs,
                    sortAlbumsBy = sortAlbumsBy,
                    sortAlbumsInReverse = sortAlbumsInReverse,
                )
            }
        }

    private fun fetchAlbumsFrom(
        songs: List<Song>,
        sortAlbumsBy: SortAlbumsBy,
        sortAlbumsInReverse: Boolean,
    ): List<Album> {
        val songsGroupedByAlbumId = songs.groupBy { it.albumId }
        val albums = songsGroupedByAlbumId.map { ( albumId, songsInAlbum ) ->
            val albumTitle = songsInAlbum.firstOrNull {
                !it.albumTitle.isNullOrBlank()
            }?.albumTitle ?: ""
            val artworkUri = songsInAlbum.firstOrNull { it.artworkUri != null }?.artworkUri
            val albumArtist = songsInAlbum.firstOrNull {
                !it.albumArtist.isNullOrBlank()
            }?.albumArtist
            val artistId = songsInAlbum.first().artistId
            Album(
                id = albumId,
                title = albumTitle,
                trackCount = songsInAlbum.size,
                artworkUri = artworkUri,
                artist = albumArtist,
                artistId = artistId
            )
        }
        return albums.sortAlbums(
            by = sortAlbumsBy,
            reverse = sortAlbumsInReverse
        )
    }

}

