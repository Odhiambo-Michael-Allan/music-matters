package com.odesa.musicMatters.di

import android.content.Context
import com.odesa.musicMatters.core.common.di.CommonDiModule
import com.odesa.musicMatters.core.data.di.DataDiModule
import kotlinx.coroutines.Dispatchers

class MobileDiModule( context: Context ) {
    private val dataDiModule = DataDiModule.getInstance(
        context = context,
        dispatcher = Dispatchers.Main
    )
    private val commonDiModule = CommonDiModule(
        context = context,
        playlistRepository = dataDiModule.playlistRepository,
        settingsRepository = dataDiModule.settingsRepository,
        songsAdditionalMetadataRepository = dataDiModule.songsAdditionalMetadataRepository
    )
    val settingsRepository = dataDiModule.settingsRepository
    val playlistRepository = dataDiModule.playlistRepository
    val searchHistoryRepository = dataDiModule.searchHistoryRepository
    val songsAdditionalMetadataRepository = dataDiModule.songsAdditionalMetadataRepository
    val musicServiceConnection = commonDiModule.musicServiceConnection
}