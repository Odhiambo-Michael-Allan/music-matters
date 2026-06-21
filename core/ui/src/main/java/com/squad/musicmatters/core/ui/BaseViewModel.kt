package com.squad.musicmatters.core.ui

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

abstract class BaseViewModel(
    private val player: MusicMattersPlayer,
    private val preferencesDataSource: PreferencesDataSource,
    private val playlistRepository: PlaylistRepository,
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

    @OptIn(UnstableApi::class)
    fun playSongs(
        selectedSong: Song,
        songsInPlaylist: List<Song>
    ) {
        player.playSong(
            song = selectedSong,
            songs = songsInPlaylist,
        )
    }

    fun shuffleAndPlay(
        songs: List<Song>,
    ) {
        if ( songs.isEmpty() ) return
        viewModelScope.launch { player.shuffleAndPlay( songs ) }
    }

    fun playSong( song: Song ) {
        player.playSong(
            song = song,
            songs = listOf( song ),
        )
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
        player.playSongNext( song )
    }

    fun addSongToQueue( song: Song ) {
        player.addToQueue( song )
    }

    fun removeSongFromQueue( song: Song ) {
        player.remove( song )
    }

    fun songIsPresentInQueue( song: Song ) = player.contains( song )

}