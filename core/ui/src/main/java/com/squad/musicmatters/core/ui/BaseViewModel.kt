package com.squad.musicmatters.core.ui

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.datastore.UserDataRepository
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import kotlinx.coroutines.launch
import java.util.UUID

abstract class BaseViewModel(
    private val player: MusicMattersPlayer,
    private val userDataRepository: UserDataRepository,
    private val playlistsRepository: PlaylistsRepository,
) : ViewModel() {

    fun addToFavorites( song: Song, isFavorite: Boolean ) {
        viewModelScope.launch {
            if ( isFavorite ) playlistsRepository.addToFavorites( song )
            else playlistsRepository.removeFromFavorites( song.id )
        }
    }

    fun addSongsToPlaylist(
        playlist: Playlist,
        songs: List<Song>
    ) {
        viewModelScope.launch {
            songs.forEach {
                playlistsRepository.addSongToPlaylist( it, playlist.id )
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
        player.shuffleAndPlay( songs )
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
            playlistsRepository.savePlaylist(
                id = UUID.randomUUID().toString(),
                playlistName = playlistTitle,
                songsInPlaylist = songsToAddToPlaylist,
            )
        }
    }

    fun setSortSongsBy( by: SortSongsBy ) {
        viewModelScope.launch {
            userDataRepository.setSortSongsBy( by )
        }
    }

    fun setSortSongsInReverse( sortSongsInReverse: Boolean ) {
        viewModelScope.launch {
            userDataRepository.setSortSongsInReverse( sortSongsInReverse )
        }
    }

    fun playSongsNext( songs: List<Song> ) {
        songs.forEach { player.playSongNext( it ) }
    }

    fun playSongNext( song: Song ) {
        player.playSongNext( song )
    }

    fun addSongsToQueue( songs: List<Song> ) {
        songs.forEach { player.addToQueue( it )  }
    }

    fun addSongToQueue( song: Song ) {
        player.addToQueue( song )
    }

    fun removeSongsFromQueue( songs: List<Song> ) {
        songs.forEach { player.remove( it ) }
    }

    fun removeSongFromQueue( song: Song ) {
        player.remove( song )
    }

    fun songIsPresentInQueue( song: Song ) = player.contains( song )

    fun noSongInTheListIsPresentInTheQueue( songs: List<Song> ): Boolean {
        return songs.none { player.contains( it ) }
    }

}