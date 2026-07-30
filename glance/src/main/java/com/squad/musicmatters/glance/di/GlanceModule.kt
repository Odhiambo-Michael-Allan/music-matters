package com.squad.musicmatters.glance.di

import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.datastore.UserDataRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn( SingletonComponent::class )
interface GlanceModuleEntryPoint {
    fun songsRepository(): SongsRepository
    fun queueRepository(): QueueRepository
    fun userDataRepository(): UserDataRepository
}