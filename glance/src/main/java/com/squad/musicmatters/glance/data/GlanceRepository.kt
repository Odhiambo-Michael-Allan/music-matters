package com.squad.musicmatters.glance.data

import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.utils.combine
import com.squad.musicmatters.core.datastore.UserDataRepository
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GlanceRepository @Inject constructor(
    private val songsRepository: SongsRepository,
    private val userDataRepository: UserDataRepository,
    private val playlistsRepository: PlaylistsRepository,
    private val player: MusicMattersPlayer,
    @param:Dispatcher( MusicMattersDispatchers.IO )
    private val ioDispatcher: CoroutineDispatcher,
) {
    fun getGlanceUiModel(): Flow<GlanceUiModel> = combine(
        songsRepository.fetchSongs( sortSongsInReverse = true ),
        userDataRepository.userData.map { it.currentlyPlayingSongId },
        userDataRepository.userData.map { it.shuffle },
        userDataRepository.userData.map { it.loopMode },
        playlistsRepository.fetchFavorites(),
        player.playerState
    ) { songs, currentlyPlayingSongId, shuffle, loopMode, favorites, playerState ->
        val currentlyPlayingSong = songs.find { it.id == currentlyPlayingSongId }
        GlanceUiModel(
            isPlaying = playerState.isPlaying,
            shuffle = shuffle,
            loopMode = loopMode,
            songs = songs.map {
                GlanceSong(
                    id = it.id,
                    mediaStoreId = it.mediaStoreId,
                    title = it.title,
                    artist = it.artist,
                    artworkUri = it.artworkUri,
                )
            },
            currentlyPlayingSong = currentlyPlayingSong?.let {
                GlanceSong(
                    id = it.id,
                    mediaStoreId = it.mediaStoreId,
                    title = it.title,
                    artist = it.artist,
                    artworkUri = it.artworkUri,
                )
            },
            currentlyPlayingSongIsFavorite = currentlyPlayingSong?.let {
                favorites?.songIds?.contains( it.id )
            } ?: false
        )
    }
        .distinctUntilChanged()
        .flowOn( ioDispatcher )

}