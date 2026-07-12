package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.AlbumsRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.utils.sortAlbums
import com.squad.musicmatters.core.datastore.DefaultPreferences
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
            val albums = songsGroupedByAlbumId.map { ( albumId, songsInAlbum ) ->
                val albumTitle = songsInAlbum.firstOrNull {
                    !it.albumTitle.isNullOrBlank()
                }?.albumTitle ?: ""
                val artworkUri = songsInAlbum.firstOrNull { it.artworkUri != null }?.artworkUri
                val albumArtist = songsInAlbum.firstOrNull {
                    !it.albumArtist.isNullOrBlank()
                }?.albumArtist
                Album(
                    id = albumId,
                    title = albumTitle,
                    trackCount = songsInAlbum.size,
                    artworkUri = artworkUri,
                    artist = albumArtist
                )
            }
            albums.sortAlbums(
                by = sortAlbumsBy ?: DefaultPreferences.SORT_ALBUMS_BY,
                reverse = sortAlbumsInReverse ?: false
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
                artist = songsInAlbum.firstOrNull { it.albumArtist != null }?.albumArtist
            )
        }


}

