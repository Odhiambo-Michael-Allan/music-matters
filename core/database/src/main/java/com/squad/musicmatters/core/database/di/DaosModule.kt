package com.squad.musicmatters.core.database.di

import com.squad.musicmatters.core.database.MusicMattersDatabase
import com.squad.musicmatters.core.database.dao.PlayHistoryDao
import com.squad.musicmatters.core.database.dao.PlaylistDao
import com.squad.musicmatters.core.database.dao.PlaylistEntryDao
import com.squad.musicmatters.core.database.dao.QueueDao
import com.squad.musicmatters.core.database.dao.SongAdditionalMetadataDao
import com.squad.musicmatters.core.database.dao.SongPlayCountEntryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn( SingletonComponent::class )
internal object DaosModule {
    @Provides
    fun providesPlaylistDao(
        database: MusicMattersDatabase
    ): PlaylistDao = database.playlistDao()

    @Provides
    fun providesPlayHistoryDao(
        database: MusicMattersDatabase
    ): PlayHistoryDao = database.playHistoryDao()

    @Provides
    fun providesPlaylistEntryDao(
        database: MusicMattersDatabase
    ): PlaylistEntryDao = database.playlistEntryDao()

    @Provides
    fun providesQueueDao(
        database: MusicMattersDatabase
    ): QueueDao = database.queueDao()

    @Provides
    fun providesSongAdditionalMetadataDao(
        database: MusicMattersDatabase
    ): SongAdditionalMetadataDao = database.songAdditionalMetadataDao()

    @Provides
    fun providesSongPlayCountEntryDao(
        database: MusicMattersDatabase
    ): SongPlayCountEntryDao = database.songPlayCountEntryDao()
}