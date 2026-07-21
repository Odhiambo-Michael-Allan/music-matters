package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.data.SearchRepository
import com.squad.musicmatters.core.data.repository.AlbumsRepository
import com.squad.musicmatters.core.data.repository.ArtistsRepository
import com.squad.musicmatters.core.data.repository.GenresRepository
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.utils.combine
import com.squad.musicmatters.core.model.SearchFilter
import com.squad.musicmatters.core.model.UserData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "SEARCH-REPOSITORY"

class SearchRepositoryImpl @Inject constructor(
    private val songsRepository: SongsRepository,
    private val albumsRepository: AlbumsRepository,
    private val artistsRepository: ArtistsRepository,
    private val genresRepository: GenresRepository,
    private val playlistsRepository: PlaylistsRepository,
    @param:Dispatcher( MusicMattersDispatchers.IO )
    private val ioDispatcher: CoroutineDispatcher,
) : SearchRepository {

    override fun search(
        query: String,
        selectedSearchFilter: SearchFilter,
        userData: UserData
    ): Flow<Map<SearchFilter, List<Any>>> = flow {
        Timber.tag( TAG ).d( "QUERY: $query" )
        if ( query.isBlank() ) {
            emit( emptyMap() )
            return@flow
        }

        val songsFlow = if ( selectedSearchFilter == SearchFilter.SONGS
            || selectedSearchFilter == SearchFilter.ALL ) {
            songsRepository.searchSongsMatching(
                query = query,
                sortSongsBy = userData.sortSongsBy,
                sortSongsInReverse = userData.sortSongsReverse,
            )
        } else flowOf( emptyList() )

        Timber.tag( TAG ).d( "SONGS FLOW: $songsFlow" )

        val albumsFlow = if ( selectedSearchFilter == SearchFilter.ALBUMS
            || selectedSearchFilter == SearchFilter.ALL ) {
            albumsRepository.searchAlbumsMatching(
                query = query,
                sortAlbumsBy = userData.sortAlbumsBy,
                sortAlbumsInReverse = userData.sortAlbumsReverse,
            )
        } else flowOf( emptyList() )

        val artistsFlow = if ( selectedSearchFilter == SearchFilter.ARTISTS
            || selectedSearchFilter == SearchFilter.ALL ) {
            artistsRepository.searchArtistsMatching(
                query = query,
                sortArtistsBy = userData.sortArtistsBy,
                sortArtistsInReverse = userData.sortArtistsReverse,
            )
        } else flowOf( emptyList() )

        val genresFlow = if ( selectedSearchFilter == SearchFilter.GENRES
            || selectedSearchFilter == SearchFilter.ALL ) {
            genresRepository.searchGenresMatching(
                query = query,
                sortGenresBy = userData.sortGenresBy,
                reverse = userData.sortGenresReverse,
            )
        } else flowOf( emptyList() )

        val playlistsFlow = if ( selectedSearchFilter == SearchFilter.PLAYLISTS
            || selectedSearchFilter == SearchFilter.ALL ) {
            playlistsRepository.searchPlaylistsMatchingQuery(
                query = query,
                sortPlaylistsBy = userData.sortPlaylistsBy,
                sortPlaylistsInReverse = userData.sortPlaylistsReverse,
            )
        } else flowOf( emptyList() )

        val combinedFlows = combine(
            songsFlow,
            albumsFlow,
            artistsFlow,
            genresFlow,
            playlistsFlow,
        ) { songs, albums, artists, genres, playlists ->
            Timber.tag( TAG ).d( "SONGS FOUND: $songs")
            buildMap {
                if ( songs.isNotEmpty() ) put( SearchFilter.SONGS, songs )
                if ( albums.isNotEmpty() ) put( SearchFilter.ALBUMS, albums )
                if ( artists.isNotEmpty() ) put( SearchFilter.ARTISTS, artists )
                if ( genres.isNotEmpty() ) put( SearchFilter.GENRES, genres )
                if ( playlists.isNotEmpty() ) put(SearchFilter.PLAYLISTS, playlists )
            }
        }
        emitAll( combinedFlows )
    }.flowOn( ioDispatcher )

}