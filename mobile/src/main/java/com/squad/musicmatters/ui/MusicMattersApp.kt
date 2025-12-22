package com.squad.musicmatters.ui

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.squad.musicmatters.MainActivityUiState
import com.squad.musicmatters.navigation.MusicMattersNavHost

@OptIn(UnstableApi::class)
@Composable
fun MusicMattersApp(
    uiState: MainActivityUiState,
//    mainActivity: MainActivity,
//    nowPlayingViewModel: NowPlayingViewModel,
    navController: NavHostController = rememberNavController(),
//    settingsRepository: SettingsRepository,
//    preferencesDataSource: PreferencesDataSource,
//    playlistRepository: PlaylistRepository,
//    searchHistoryRepository: SearchHistoryRepository,
//    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
//    musicServiceConnection: MusicServiceConnection,
) {
    MusicMattersAppContent(
        uiState = uiState,
//        mainActivity = mainActivity,
//        nowPlayingViewModel = nowPlayingViewModel,
        navController = navController,
//        settingsRepository = settingsRepository,
//        preferencesDataSource = preferencesDataSource,
//        playlistRepository = playlistRepository,
//        searchHistoryRepository = searchHistoryRepository,
//        songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
//        musicServiceConnection = musicServiceConnection,
    )
}

@OptIn( UnstableApi::class )
@Composable
fun MusicMattersAppContent(
//    mainActivity: MainActivity,
//    nowPlayingViewModel: NowPlayingViewModel,
    uiState: MainActivityUiState,
    navController: NavHostController,
//    preferencesDataSource: PreferencesDataSource,
//    playlistRepository: PlaylistRepository,
//    searchHistoryRepository: SearchHistoryRepository,
//    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
//    musicServiceConnection: MusicServiceConnection,
) {

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        when ( uiState ) {
            MainActivityUiState.Loading -> {}
            is MainActivityUiState.Success -> {
                MusicMattersNavHost(
//            mainActivity = mainActivity,
//            nowPlayingViewModel = nowPlayingViewModel,
                    navController = navController,
//                    preferencesDataSource = preferencesDataSource,
//            settingsRepository = settingsRepository,
//                    playlistRepository = playlistRepository,
//                    searchHistoryRepository = searchHistoryRepository,
//                    songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
//                    musicServiceConnection = musicServiceConnection,
                    language = uiState.userData.language,
                    labelVisibility = uiState.userData.bottomBarLabelVisibility,
                )
            }
        }
    }
}




