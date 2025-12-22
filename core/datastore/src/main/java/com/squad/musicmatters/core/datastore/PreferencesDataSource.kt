package com.squad.musicmatters.core.datastore

import com.squad.musicmatters.core.model.BottomBarLabelVisibility
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.LyricsLayout
import com.squad.musicmatters.core.model.SortAlbumsBy
import com.squad.musicmatters.core.model.SortArtistsBy
import com.squad.musicmatters.core.model.SortGenresBy
import com.squad.musicmatters.core.model.SortPathsBy
import com.squad.musicmatters.core.model.SortPlaylistsBy
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.model.UserData
import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource {
    val userData: Flow<UserData>
    suspend fun setFontName( fontName: String )
    suspend fun setFontScale( fontScale: Float )
    suspend fun setThemeMode( themeMode: ThemeMode )
    suspend fun useMaterialYou( useMaterialYou: Boolean )
    suspend fun setPrimaryColorName( primaryColorName: String )
    suspend fun setBottomBarLabelVisibility( visibility: BottomBarLabelVisibility )
    suspend fun setFadePlayback( fadePlayback: Boolean )
    suspend fun setFadePlaybackDuration( fadePlaybackDuration: Float )
    suspend fun setRequireAudioFocus( requireAudioFocus: Boolean )
    suspend fun setIgnoreAudioFocusLoss( ignoreAudioFocusLoss: Boolean )
    suspend fun setPlayOnHeadphonesConnect( playOnHeadphonesConnect: Boolean )
    suspend fun setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect: Boolean )
    suspend fun setFastRewindDuration( fastRewindDuration: Int )
    suspend fun setFastForwardDuration( fastForwardDuration: Int )
    suspend fun setMiniPlayerTextMarquee( miniPlayerTextMarquee: Boolean )
    suspend fun setMiniPlayerShowSeekControls( miniPlayerShowSeekControls: Boolean )
    suspend fun setMiniPlayerShowTrackControls( miniPlayerShowTrackControls: Boolean )
    suspend fun setLyricsLayout( lyricsLayout: LyricsLayout )
    suspend fun setShowNowPlayingAudioInformation( showNowPlayingAudioInformation: Boolean )
    suspend fun setShowNowPlayingSeekControls( showNowPlayingSeekControls: Boolean )
    suspend fun setPlaybackSpeed( playbackSpeed: Float )
    suspend fun setPlaybackPitch( playbackPitch: Float )
    suspend fun setLoopMode( loopMode: LoopMode )
    suspend fun setShuffle( shuffle: Boolean )
    suspend fun setShowLyrics( showLyrics: Boolean )
    suspend fun setControlsLayoutIsDefault( controlsLayoutIsDefault: Boolean )
    suspend fun setDisabledTreePaths( disabledTreePaths: Set<String> )
    suspend fun setSortSongsBy( by: SortSongsBy )
    suspend fun setSortSongsInReverse( sortSongsInReverse: Boolean )
    suspend fun setSortArtistsBy( by: SortArtistsBy )
    suspend fun setSortArtistsInReverse( reverse: Boolean )
    suspend fun setSortGenresBy( by: SortGenresBy )
    suspend fun setSortGenresInReverse( reverse: Boolean )
    suspend fun setSortPlaylistsBy( by: SortPlaylistsBy )
    suspend fun setSortPlaylistsInReverse( reverse: Boolean )
    suspend fun setSortAlbumsBy( by: SortAlbumsBy )
    suspend fun setSortAlbumsInReverse( reverse: Boolean )
    suspend fun setSortPathsBy( by: SortPathsBy )
    suspend fun setSortPathsInReverse( reverse: Boolean )
    suspend fun setCurrentlyPlayingSongId( songId: String )
}