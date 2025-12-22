package com.squad.musicmatters.core.common.di

import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn( SingletonComponent::class )
internal object DispatchersModule {
    @Provides
    @Dispatcher(MusicMattersDispatchers.IO )
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher( MusicMattersDispatchers.Default )
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Dispatcher(MusicMattersDispatchers.Main )
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}