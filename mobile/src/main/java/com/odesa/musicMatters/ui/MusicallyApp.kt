package com.odesa.musicMatters.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.odesa.musicMatters.MainActivity
import com.odesa.musicMatters.core.common.connection.MusicServiceConnection
import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.search.SearchHistoryRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.ui.navigation.MusicMattersNavHost

@Composable
fun MusicallyApp(
    mainActivity: MainActivity,
    navController: NavHostController = rememberNavController(),
    settingsRepository: SettingsRepository,
    playlistRepository: PlaylistRepository,
    searchHistoryRepository: SearchHistoryRepository,
    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
    musicServiceConnection: MusicServiceConnection,
) {
    MusicallyAppContent(
        mainActivity = mainActivity,
        navController = navController,
        settingsRepository = settingsRepository,
        playlistRepository = playlistRepository,
        searchHistoryRepository = searchHistoryRepository,
        songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
        musicServiceConnection = musicServiceConnection,
    )
}

@Composable
fun MusicallyAppContent(
    mainActivity: MainActivity,
    navController: NavHostController,
    settingsRepository: SettingsRepository,
    playlistRepository: PlaylistRepository,
    searchHistoryRepository: SearchHistoryRepository,
    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
    musicServiceConnection: MusicServiceConnection,
) {

    val language by settingsRepository.language.collectAsState()
    val labelVisibility by settingsRepository.homePageBottomBarLabelVisibility.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        MusicMattersNavHost(
            mainActivity = mainActivity,
            navController = navController,
            settingsRepository = settingsRepository,
            playlistRepository = playlistRepository,
            searchHistoryRepository = searchHistoryRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
            musicServiceConnection = musicServiceConnection,
            language = language,
            labelVisibility = labelVisibility,
        )
    }
}




