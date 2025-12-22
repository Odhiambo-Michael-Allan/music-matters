package com.squad.musicmatters.core.common.di

import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn( SingletonComponent::class )
internal object CoroutineScopesModule {
    @Provides
    @Singleton
    @ApplicationScope
    fun providesCoroutineScope(
        @Dispatcher( MusicMattersDispatchers.Default ) dispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope( SupervisorJob() + dispatcher )
}

@Retention( AnnotationRetention.RUNTIME )
@Qualifier
annotation class ApplicationScope