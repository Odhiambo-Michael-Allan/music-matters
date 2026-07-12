package com.squad.musicmatters.feature.foryou

import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.data.repository.AlbumsRepository
import com.squad.musicmatters.core.data.repository.ArtistsRepository
import com.squad.musicmatters.core.data.repository.MostPlayedSongsRepository
import com.squad.musicmatters.core.data.repository.PlayHistoryRepository
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.utils.sortSongs
import com.squad.musicmatters.core.data.utils.subListNonStrict
import com.squad.musicmatters.core.datastore.UserDataRepository
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ForYouScreenViewModel @Inject constructor(
    player: MusicMattersPlayer,
    songsRepository: SongsRepository,
    albumsRepository: AlbumsRepository,
    artistsRepository: ArtistsRepository,
    userDataRepository: UserDataRepository,
    playlistsRepository: PlaylistsRepository,
    playHistoryRepository: PlayHistoryRepository,
    mostPlayedSongsRepository: MostPlayedSongsRepository,
) : BaseViewModel(
    player = player,
    userDataRepository = userDataRepository,
    playlistsRepository = playlistsRepository,
) {

    val uiState: StateFlow<ForYouScreenUiState> = com.squad.musicmatters.core.data.utils.combine(
        songsRepository.fetchSongs(),
        mostPlayedSongsRepository
            .fetchSongsSortedByPlayCount()
            .flatMapLatest { songs ->
                val mostPlayedAlbumIds = songs.map { it.albumId }.toSet()
                albumsRepository.fetchAlbums().map { albumsList ->
                    albumsList.filter { album ->
                        album.id in mostPlayedAlbumIds
                    }
                }
            },
        mostPlayedSongsRepository.fetchSongsSortedByPlayCount(),
        mostPlayedSongsRepository
            .fetchSongsSortedByPlayCount()
            .flatMapLatest { songs ->
                val mostPlayedArtistsIds = songs.map { it.artistId }.toSet()
                artistsRepository.fetchArtists().map { artistsList ->
                    artistsList.filter { artist ->
                        artist.id in mostPlayedArtistsIds
                    }
                }
            },
        playHistoryRepository.fetchSongsSortedByTimePlayed(),
        playlistsRepository.fetchPlaylists()
    ) { songs, suggestedAlbums, mostPlayedSongs, suggestedArtists, playHistory, playlists ->
        ForYouScreenUiState.Success(
            recentlyAddedSongs = songs.sortSongs(
                by = SortSongsBy.TITLE,
                reverse = false
            ).subListNonStrict( 10 ),
            suggestedAlbums = suggestedAlbums.subListNonStrict( 10 ),
            mostPlayedSongs = mostPlayedSongs,
            suggestedArtists = suggestedArtists.subListNonStrict( 10 ),
            recentlyPlayedSongs = playHistory,
            playlists = playlists
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed( 5_000 ),
        initialValue = ForYouScreenUiState.Loading,
    )

}

sealed interface ForYouScreenUiState {
    data object Loading : ForYouScreenUiState
    data class Success(
        val recentlyAddedSongs: List<Song>,
        val suggestedAlbums: List<Album>,
        val mostPlayedSongs: List<Song>,
        val suggestedArtists: List<Artist>,
        val recentlyPlayedSongs: List<Song>,
        val playlists: List<Playlist>
    ): ForYouScreenUiState
}