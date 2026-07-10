package com.squad.musicmatters.feature.artist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.squad.musicmatters.core.data.repository.ArtistsRepository
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.datastore.UserPreferencesRepository
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.ui.BaseViewModel
import com.squad.musicmatters.feature.artist.navigation.ArtistRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ArtistScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    artistsRepository: ArtistsRepository,
    songsRepository: SongsRepository,
    player: MusicMattersPlayer,
    userPreferencesRepository: UserPreferencesRepository,
    playlistsRepository: PlaylistsRepository,
    songsMetadataRepository: SongsMetadataRepository,
) : BaseViewModel(
    player = player,
    userPreferencesRepository = userPreferencesRepository,
    playlistsRepository = playlistsRepository
) {

    val artistId = savedStateHandle.toRoute<ArtistRoute>().artistId

    val uiState: StateFlow<ArtistScreenUiState> = com.squad.musicmatters.core.data.utils.combine(
        artistsRepository.fetchArtistWithId( artistId ),
        userPreferencesRepository.userData.flatMapLatest {
            songsRepository.fetchSongs(
                sortSongsBy = it.sortSongsBy,
                sortSongsInReverse = it.sortSongsReverse
            )
        },
        userPreferencesRepository.userData,
        playlistsRepository.fetchFavorites(),
        playlistsRepository.fetchPlaylists(),
        songsMetadataRepository.fetchMetadata()
    ) { artist, songs, userData, favoriteSongsPlaylist, playlists, metadata ->
        ArtistScreenUiState.Success(
            artist = artist,
            songsByArtist = songs.filter { it.artistId == artistId },
            sortSongsBy = userData.sortSongsBy,
            sortSongsInReverse = userData.sortSongsReverse,
            favoriteSongIds = favoriteSongsPlaylist?.songIds ?: emptySet(),
            playlists = playlists,
            songsMetadata = metadata,
            currentlyPlayingSongId = userData.currentlyPlayingSongId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed( 5_000 ),
        initialValue = ArtistScreenUiState.Loading
    )

}

sealed interface ArtistScreenUiState {
    data object Loading : ArtistScreenUiState
    data class Success(
        val artist: Artist,
        val songsByArtist: List<Song>,
        val sortSongsBy: SortSongsBy,
        val sortSongsInReverse: Boolean,
        val currentlyPlayingSongId: String,
        val favoriteSongIds: Set<String>,
        val playlists: List<Playlist>,
        val songsMetadata: List<SongMetadata>
    ): ArtistScreenUiState
}