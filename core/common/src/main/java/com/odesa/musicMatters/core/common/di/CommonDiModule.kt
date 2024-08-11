package com.odesa.musicMatters.core.common.di

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.util.UnstableApi
import com.odesa.musicMatters.core.common.connection.MediaBrowserAdapter
import com.odesa.musicMatters.core.common.connection.MusicServiceConnection
import com.odesa.musicMatters.core.common.connection.MusicServiceConnectionImpl
import com.odesa.musicMatters.core.common.media.MusicService
import com.odesa.musicMatters.core.data.preferences.toRepeatMode
import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import kotlinx.coroutines.Dispatchers


@UnstableApi
class CommonDiModule(
    context: Context,
    playlistRepository: PlaylistRepository,
    settingsRepository: SettingsRepository,
    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
) {

    private val mediaBrowserAdapter = MediaBrowserAdapter(
        context = context,
        serviceComponentName = ComponentName( context, MusicService::class.java )
    )
    val musicServiceConnection: MusicServiceConnection =
        MusicServiceConnectionImpl.getInstance(
            connectable = mediaBrowserAdapter,
            playlistRepository = playlistRepository,
            settingsRepository = settingsRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
            dispatcher = Dispatchers.Main,
            playbackParameters = PlaybackParameters(
                settingsRepository.currentPlaybackSpeed.value,
                settingsRepository.currentPlaybackPitch.value
            ),
            repeatMode = settingsRepository.currentLoopMode.value.toRepeatMode(),
        )
}

