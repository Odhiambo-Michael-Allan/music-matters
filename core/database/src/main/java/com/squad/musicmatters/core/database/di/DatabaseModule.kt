package com.squad.musicmatters.core.database.di

import android.content.Context
import androidx.room.Room
import com.squad.musicmatters.core.database.MusicMattersDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn( SingletonComponent::class )
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesMusicMattersDatabase(
        @ApplicationContext context: Context
    ): MusicMattersDatabase = Room.databaseBuilder(
        context = context,
        MusicMattersDatabase::class.java,
        name = "music-matters-database"
    ).build()
}