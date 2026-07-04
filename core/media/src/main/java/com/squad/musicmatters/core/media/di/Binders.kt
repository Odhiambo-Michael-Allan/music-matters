package com.squad.musicmatters.core.media.di

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.squad.musicmatters.core.media.connection.DefaultSongToMediaItemConverter
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.media.connection.MusicMattersPlayerImpl
import com.squad.musicmatters.core.media.connection.SongToMediaItemConverter
import com.squad.musicmatters.core.media.media.PlaybackPositionUpdater
import com.squad.musicmatters.core.media.media.PlaybackPositionUpdaterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn( SingletonComponent::class )
@OptIn( UnstableApi::class )
abstract class MediaDiModuleBinders {

    @Binds
    @Singleton // THIS IS CRUCIAL!!
    abstract fun bindsMusicServiceConnection(
        connection: MusicMattersPlayerImpl
    ): MusicMattersPlayer

    @Binds
    abstract fun bindsSongToMediaItemConverter(
        converter: DefaultSongToMediaItemConverter
    ): SongToMediaItemConverter

    @Binds
    abstract fun bindsPlaybackPositionUpdater(
        updater: PlaybackPositionUpdaterImpl
    ): PlaybackPositionUpdater

}