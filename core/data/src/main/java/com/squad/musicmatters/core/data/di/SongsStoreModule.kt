package com.squad.musicmatters.core.data.di

import android.content.Context
import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.common.di.ApplicationScope
import com.squad.musicmatters.core.data.songs.SongsStore
import com.squad.musicmatters.core.data.songs.impl.SongsStoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn( SingletonComponent::class )
internal object SongsStoreModule {

    @Provides
    @Singleton
    internal fun providesSongsStore(
        @ApplicationContext context: Context,
        @Dispatcher(MusicMattersDispatchers.IO ) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope applicationScope: CoroutineScope
    ): SongsStore = SongsStoreImpl(
        context = context,
        ioDispatcher = ioDispatcher,
        applicationScope = applicationScope,
    )

}