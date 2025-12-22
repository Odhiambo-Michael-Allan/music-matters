package com.squad.musicmatters.core.media.di

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.squad.musicmatters.core.media.connection.DefaultSongToMediaItemConverter
import com.squad.musicmatters.core.media.connection.MusicServiceConnection
import com.squad.musicmatters.core.media.connection.MusicServiceConnectionImpl
import com.squad.musicmatters.core.media.connection.SongToMediaItemConverter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn( SingletonComponent::class )
@OptIn( UnstableApi::class )
abstract class MediaDiModuleBinders {

    @Binds
    abstract fun bindsMusicServiceConnection(
        connection: MusicServiceConnectionImpl
    ): MusicServiceConnection

    @Binds
    abstract fun bindsSongToMediaItemConverter(
        converter: DefaultSongToMediaItemConverter
    ): SongToMediaItemConverter

}