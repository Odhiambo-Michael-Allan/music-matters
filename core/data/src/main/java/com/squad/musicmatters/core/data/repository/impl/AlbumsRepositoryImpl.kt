package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.AlbumsRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.SortAlbumsBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.firstOrNull

class AlbumsRepositoryImpl @Inject constructor(
    private val songsRepository: SongsRepository
): AlbumsRepository {

    override fun fetchAlbums(
        sortAlbumsBy: SortAlbumsBy?,
        sortAlbumsInReverse: Boolean?
    ): Flow<List<Album>> = songsRepository.fetchSongs()
        .map { songs ->
            val songsGroupedByAlbumId = songs.groupBy { it.albumId }
            songsGroupedByAlbumId.map { (albumId, songsInAlbum) ->
                val albumTitle = songsInAlbum.firstOrNull()?.albumTitle ?: ""
                val artworkUri = songsInAlbum.firstOrNull { it.artworkUri != null }?.artworkUri
                Album(
                    id = albumId,
                    title = albumTitle,
                    trackCount = songsInAlbum.size,
                    artworkUri = artworkUri,
                    albumArtist = songsInAlbum.firstOrNull { it.albumArtist != null }?.albumArtist
                )
            }
        }

    override fun fetchAlbumWithId( id: Long ): Flow<Album> = songsRepository.fetchSongs()
        .map { songs ->
            val songsInAlbum = songs.filter { it.albumId == id }
            val albumTitle = songsInAlbum.firstOrNull()?.albumTitle ?: ""
            Album(
                id = id,
                title = albumTitle,
                trackCount = songsInAlbum.size,
                artworkUri = songsInAlbum.firstOrNull { it.artworkUri != null }?.artworkUri,
                albumArtist = songsInAlbum.firstOrNull { it.albumArtist != null }?.albumArtist
            )
        }

}