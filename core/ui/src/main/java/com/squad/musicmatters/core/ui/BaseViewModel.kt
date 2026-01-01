package com.squad.musicmatters.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.media.connection.MusicServiceConnection
import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

abstract class BaseViewModel(
    private val player: MusicServiceConnection,
    private val preferencesDataSource: PreferencesDataSource,
    private val playlistRepository: PlaylistRepository,
    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
) : ViewModel() {

    fun addToFavorites( song: Song, isFavorite: Boolean ) {
        viewModelScope.launch {
            if ( isFavorite ) playlistRepository.addToFavorites( song )
            else playlistRepository.removeFromFavorites( song.id )
        }
    }

    fun addSongsToPlaylist(
        playlist: Playlist,
        songs: List<Song>
    ) {
        viewModelScope.launch {
            songs.forEach {
                playlistRepository.addSongToPlaylist( it, playlist.id )
            }
        }
    }

    fun playSongs(
        selectedSong: Song,
        songsInPlaylist: List<Song>
    ) {
        viewModelScope.launch {
            player.playSong(
                song = selectedSong,
                songs = songsInPlaylist,
                shuffle = preferencesDataSource.userData.first().shuffle
            )
        }
    }

    fun shuffleAndPlay(
        songs: List<Song>,
    ) {
        if ( songs.isEmpty() ) return
        viewModelScope.launch { player.shuffleAndPlay( songs ) }
    }

    fun playSong( song: Song ) {
        viewModelScope.launch {
            player.playSong(
                song = song,
                songs = listOf( song ),
                shuffle = false // Its only one, no need to shuffle..
            )
        }
    }

    fun createPlaylist(
        playlistTitle: String,
        songsToAddToPlaylist: List<Song>
    ) {
        viewModelScope.launch {
            playlistRepository.savePlaylist(
                id = UUID.randomUUID().toString(),
                playlistName = playlistTitle,
                songsInPlaylist = songsToAddToPlaylist,
            )
        }
    }

    fun setSortSongsBy( by: SortSongsBy ) {
        viewModelScope.launch {
            preferencesDataSource.setSortSongsBy( by )
        }
    }

    fun setSortSongsInReverse( sortSongsInReverse: Boolean ) {
        viewModelScope.launch {
            preferencesDataSource.setSortSongsInReverse( sortSongsInReverse )
        }
    }

    fun playSongNext( song: Song ) {
        viewModelScope.launch { player.playNext( song ) }
    }

    fun addSongToQueue( song: Song ) {
        viewModelScope.launch { player.addToQueue( song ) }
    }

}