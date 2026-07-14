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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

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
    ): Flow<Map<SearchFilter, List<Any>>> {
        if ( query.isBlank() ) {
            return flowOf( emptyMap() )
        }

        val songsFlow = if ( selectedSearchFilter == SearchFilter.SONGS ) {
            songsRepository.searchSongsMatching(
                query = query,
                sortSongsBy = userData.sortSongsBy,
                sortSongsInReverse = userData.sortSongsReverse,
            )
        } else flowOf( emptyList() )

        val albumsFlow = if ( selectedSearchFilter == SearchFilter.ALBUMS ) {
            albumsRepository.searchAlbumsMatching(
                query = query,
                sortAlbumsBy = userData.sortAlbumsBy,
                sortAlbumsInReverse = userData.sortAlbumsReverse,
            )
        } else flowOf( emptyList() )

        val artistsFlow = if ( selectedSearchFilter == SearchFilter.ARTISTS ) {
            artistsRepository.searchArtistsMatching(
                query = query,
                sortArtistsBy = userData.sortArtistsBy,
                sortArtistsInReverse = userData.sortArtistsReverse,
            )
        } else flowOf( emptyList() )

        val genresFlow = if ( selectedSearchFilter == SearchFilter.GENRES ) {
            genresRepository.searchGenresMatching(
                query = query,
                sortGenresBy = userData.sortGenresBy,
                reverse = userData.sortGenresReverse,
            )
        } else flowOf( emptyList() )

        val playlistsFlow = if ( selectedSearchFilter == SearchFilter.PLAYLISTS ) {
            playlistsRepository.searchPlaylistsMatchingQuery(
                query = query,
                sortPlaylistsBy = userData.sortPlaylistsBy,
                sortPlaylistsInReverse = userData.sortPlaylistsReverse,
            )
        } else flowOf( emptyList() )

        return combine(
            songsFlow,
            albumsFlow,
            artistsFlow,
            genresFlow,
            playlistsFlow,
        ) { songs, albums, artists, genres, playlists ->
            buildMap {
                if ( songs.isNotEmpty() ) put( SearchFilter.SONGS, songs )
                if ( albums.isNotEmpty() ) put( SearchFilter.ALBUMS, albums )
                if ( artists.isNotEmpty() ) put( SearchFilter.ARTISTS, artists )
                if ( genres.isNotEmpty() ) put( SearchFilter.GENRES, genres )
                if ( playlists.isNotEmpty() ) put(SearchFilter.PLAYLISTS, playlists )
            }
        }.flowOn( ioDispatcher )
    }

}