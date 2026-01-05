package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.model.BottomBarLabelVisibility
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.SortAlbumsBy
import com.squad.musicmatters.core.model.SortArtistsBy
import com.squad.musicmatters.core.model.SortGenresBy
import com.squad.musicmatters.core.model.SortPathsBy
import com.squad.musicmatters.core.model.SortPlaylistsBy
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.model.UserData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull

class TestPreferencesDataSource : PreferencesDataSource {

    private val _userData = MutableSharedFlow<UserData>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val currentUserData = _userData.replayCache.firstOrNull() ?: emptyUserData

    override val userData: Flow<UserData> = _userData.filterNotNull()

    override suspend fun setFontName( fontName: String ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    fontName = fontName
                )
            )
        }
    }

    override suspend fun setFontScale( fontScale: Float ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    fontScale = fontScale
                )
            )
        }
    }

    override suspend fun setThemeMode( themeMode: ThemeMode ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    themeMode = themeMode
                )
            )
        }
    }

    override suspend fun useMaterialYou( useMaterialYou: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    useMaterialYou = useMaterialYou
                )
            )
        }
    }

    override suspend fun setPrimaryColorName( primaryColorName: String ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    primaryColorName = primaryColorName
                )
            )
        }
    }

    override suspend fun setBottomBarLabelVisibility( visibility: BottomBarLabelVisibility ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    bottomBarLabelVisibility = visibility
                )
            )
        }
    }

    override suspend fun setFadePlayback( fadePlayback: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    fadePlayback = fadePlayback
                )
            )
        }
    }

    override suspend fun setFadePlaybackDuration( fadePlaybackDuration: Float ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    fadePlaybackDuration = fadePlaybackDuration
                )
            )
        }
    }

    override suspend fun setRequireAudioFocus( requireAudioFocus: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    requireAudioFocus = requireAudioFocus
                )
            )
        }
    }

    override suspend fun setIgnoreAudioFocusLoss( ignoreAudioFocusLoss: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    ignoreAudioFocusLoss = ignoreAudioFocusLoss
                )
            )
        }
    }

    override suspend fun setPlayOnHeadphonesConnect( playOnHeadphonesConnect: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    playOnHeadphonesConnect = playOnHeadphonesConnect
                )
            )
        }
    }

    override suspend fun setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    pauseOnHeadphonesDisconnect = pauseOnHeadphonesDisconnect
                )
            )
        }
    }

    override suspend fun setFastRewindDuration( fastRewindDuration: Int ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    fastRewindDuration = fastRewindDuration
                )
            )
        }
    }

    override suspend fun setFastForwardDuration( fastForwardDuration: Int ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    fastForwardDuration = fastForwardDuration
                )
            )
        }
    }

    override suspend fun setMiniPlayerTextMarquee( miniPlayerTextMarquee: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    miniPlayerTextMarquee = miniPlayerTextMarquee
                )
            )
        }
    }

    override suspend fun setMiniPlayerShowSeekControls( miniPlayerShowSeekControls: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    miniPlayerShowSeekControls = miniPlayerShowSeekControls
                )
            )
        }
    }

    override suspend fun setMiniPlayerShowTrackControls( miniPlayerShowTrackControls: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    miniPlayerShowTrackControls = miniPlayerShowTrackControls
                )
            )
        }
    }

    override suspend fun setPlaybackSpeed( playbackSpeed: Float ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    playbackSpeed = playbackSpeed
                )
            )
        }
    }

    override suspend fun setPlaybackPitch( playbackPitch: Float ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    playbackPitch = playbackPitch
                )
            )
        }
    }

    override suspend fun setLoopMode( loopMode: LoopMode ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    loopMode = loopMode
                )
            )
        }
    }

    override suspend fun setShuffle( shuffle: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    shuffle = shuffle
                )
            )
        }
    }

    override suspend fun setShowLyrics( showLyrics: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    showLyrics = showLyrics
                )
            )
        }
    }

    override suspend fun setControlsLayoutIsDefault( controlsLayoutIsDefault: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    controlsLayoutDefault = controlsLayoutIsDefault
                )
            )
        }
    }

    override suspend fun setDisabledTreePaths( disabledTreePaths: Set<String> ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    currentlyDisabledTreePaths = disabledTreePaths
                )
            )
        }
    }

    override suspend fun setSortSongsBy( by: SortSongsBy ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    sortSongsBy = by
                )
            )
        }
    }

    override suspend fun setSortSongsInReverse( sortSongsInReverse: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    sortSongsReverse = sortSongsInReverse
                )
            )
        }
    }

    override suspend fun setSortArtistsBy( by: SortArtistsBy ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    sortArtistsBy = by
                )
            )
        }
    }

    override suspend fun setSortArtistsInReverse( reverse: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    sortArtistsReverse = reverse
                )
            )
        }
    }

    override suspend fun setSortGenresBy( by: SortGenresBy ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    sortGenresBy = by
                )
            )
        }
    }

    override suspend fun setSortGenresInReverse( reverse: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    sortGenresReverse = reverse
                )
            )
        }
    }

    override suspend fun setSortPlaylistsBy( by: SortPlaylistsBy ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    sortPlaylistsBy = by
                )
            )
        }
    }

    override suspend fun setSortPlaylistsInReverse( reverse: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    sortPlaylistsReverse = reverse
                )
            )
        }
    }

    override suspend fun setSortAlbumsBy( by: SortAlbumsBy ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    sortAlbumsBy = by
                )
            )
        }
    }

    override suspend fun setSortAlbumsInReverse( reverse: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    sortAlbumsReverse = reverse
                )
            )
        }
    }

    override suspend fun setSortPathsBy( by: SortPathsBy ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    sortPathsBy = by
                )
            )
        }
    }

    override suspend fun setSortPathsInReverse( reverse: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    sortPathsReverse = reverse
                )
            )
        }
    }

    override suspend fun setCurrentlyPlayingSongId( songId: String ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    currentlyPlayingSongId = songId
                )
            )
        }
    }

    fun sendUserData( userData: UserData ) {
        _userData.tryEmit( userData )
    }
}

val emptyUserData = UserData(
    fontName = "Product Sans",
    fontScale = 1f,
    themeMode = ThemeMode.FOLLOW_SYSTEM,
    useMaterialYou = true,
    primaryColorName = "Blue",
    bottomBarLabelVisibility = BottomBarLabelVisibility.ALWAYS_VISIBLE,
    fadePlayback = true,
    fadePlaybackDuration = 1f,
    requireAudioFocus = true,
    ignoreAudioFocusLoss = false,
    playOnHeadphonesConnect = true,
    pauseOnHeadphonesDisconnect = false,
    fastRewindDuration = 30,
    fastForwardDuration = 30,
    miniPlayerShowTrackControls = true,
    miniPlayerShowSeekControls = false,
    miniPlayerTextMarquee = true,
    playbackSpeed = 1f,
    playbackPitch = 1f,
    loopMode = LoopMode.None,
    shuffle = false,
    showLyrics = false,
    controlsLayoutDefault = true,
    currentlyDisabledTreePaths = emptySet(),
    sortSongsBy = SortSongsBy.TITLE,
    sortSongsReverse = false,
    sortArtistsBy = SortArtistsBy.ARTIST_NAME,
    sortArtistsReverse = false,
    sortGenresBy = SortGenresBy.NAME,
    sortGenresReverse = false,
    sortPlaylistsBy = SortPlaylistsBy.TITLE,
    sortPlaylistsReverse = false,
    sortAlbumsBy = SortAlbumsBy.ALBUM_NAME,
    sortAlbumsReverse = false,
    sortPathsBy = SortPathsBy.NAME,
    sortPathsReverse = false,
    currentlyPlayingSongId = "",
)