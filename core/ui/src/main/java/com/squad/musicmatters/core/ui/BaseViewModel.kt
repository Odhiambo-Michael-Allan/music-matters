package com.squad.musicmatters.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.squad.musicmatters.core.media.connection.MusicServiceConnection
import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
import com.squad.musicmatters.core.data.utils.FuzzySearchOption
import com.squad.musicmatters.core.data.utils.FuzzySearcher
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.PlaylistInfo
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

abstract class BaseViewModel(
    private val player: MusicServiceConnection,
    private val preferencesDataSource: PreferencesDataSource,
    private val playlistRepository: PlaylistRepository,
    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
) : ViewModel() {

    fun addToFavorites( songId: String ) {
        viewModelScope.launch { playlistRepository.addToFavorites( songId ) }
    }

    fun addSongsToPlaylist(
        playlistInfo: PlaylistInfo,
        songs: List<Song>
    ) {
        viewModelScope.launch {
            songs.forEach {
                playlistRepository.addSongIdToPlaylist( it.id, playlistInfo.id )
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
                shuffle = false
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
                PlaylistInfo(
                    id = UUID.randomUUID().toString(),
                    title = playlistTitle,
                    songIds = songsToAddToPlaylist.map { it.id }.toSet()
                )
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