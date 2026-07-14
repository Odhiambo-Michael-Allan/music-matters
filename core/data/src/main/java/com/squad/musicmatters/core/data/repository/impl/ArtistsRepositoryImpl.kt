package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.ArtistsRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.utils.sortArtists
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortAlbumsBy
import com.squad.musicmatters.core.model.SortArtistsBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

class ArtistsRepositoryImpl @Inject constructor(
    private val songsRepository: SongsRepository
) : ArtistsRepository {

    override fun fetchArtists(
        sortArtistsBy: SortArtistsBy?,
        sortArtistsInReverse: Boolean
    ): Flow<List<Artist>> = songsRepository.fetchSongs()
        .map { songs ->
            fetchArtistsFrom(
                songs = songs,
                sortArtistsBy = sortArtistsBy ?: DefaultPreferences.SORT_ARTISTS_BY,
                sortArtistsInReverse = sortArtistsInReverse
            )
        }

    override fun searchArtistsMatching(
        query: String,
        sortArtistsBy: SortArtistsBy?,
        sortArtistsInReverse: Boolean
    ): Flow<List<Artist>> =
        if ( query.isBlank() ) {
            flowOf( emptyList() )
        } else {
            songsRepository.searchSongsByArtistMatching( query ).map { songs ->
                fetchArtistsFrom(
                    songs = songs,
                    sortArtistsBy = sortArtistsBy ?: DefaultPreferences.SORT_ARTISTS_BY,
                    sortArtistsInReverse = sortArtistsInReverse,
                )
            }
        }

    private fun fetchArtistsFrom(
        songs: List<Song>,
        sortArtistsBy: SortArtistsBy,
        sortArtistsInReverse: Boolean = false,
    ): List<Artist> {
        val songsGroupedByArtistId = songs.groupBy { it.artistId }
        val artists = songsGroupedByArtistId.map { ( artistId, songsByArtist ) ->
            val artistName = songsByArtist.firstOrNull {
                it.artist.isNotBlank()
            }?.artist ?: ""
            val artworkUri = songsByArtist.firstOrNull { it.artworkUri != null }?.artworkUri
            Artist(
                id = artistId,
                name = artistName,
                artworkUri = artworkUri,
                trackCount = songsByArtist.size
            )
        }
        return artists.sortArtists(
            by = sortArtistsBy,
            reverse = sortArtistsInReverse
        )
    }


    override fun fetchArtistWithId( id: Long ): Flow<Artist> = songsRepository.fetchSongs()
        .map { songs ->
            val songsByArtist = songs.filter { it.artistId == id }
            val artistName = songsByArtist
                .firstOrNull { it.artist.isNotBlank() }?.artist ?: ""
            Artist(
                id = id,
                name = artistName,
                artworkUri = songsByArtist.firstOrNull { it.artworkUri != null }?.artworkUri,
                trackCount = songsByArtist.size
            )
        }

}

