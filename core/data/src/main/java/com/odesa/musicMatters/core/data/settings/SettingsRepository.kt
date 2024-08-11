package com.odesa.musicMatters.core.data.settings

import com.odesa.musicMatters.core.data.preferences.HomePageBottomBarLabelVisibility
import com.odesa.musicMatters.core.data.preferences.LoopMode
import com.odesa.musicMatters.core.data.preferences.NowPlayingLyricsLayout
import com.odesa.musicMatters.core.data.preferences.SortAlbumsBy
import com.odesa.musicMatters.core.data.preferences.SortArtistsBy
import com.odesa.musicMatters.core.data.preferences.SortGenresBy
import com.odesa.musicMatters.core.data.preferences.SortPathsBy
import com.odesa.musicMatters.core.data.preferences.SortPlaylistsBy
import com.odesa.musicMatters.core.data.preferences.SortSongsBy
import com.odesa.musicMatters.core.designsystem.theme.MusicallyFont
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.Language
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val language: StateFlow<Language>
    val font: StateFlow<MusicallyFont>
    val fontScale: StateFlow<Float>

    val themeMode: StateFlow<ThemeMode>
    val useMaterialYou: StateFlow<Boolean>
    val primaryColorName: StateFlow<String>

    val homePageBottomBarLabelVisibility: StateFlow<HomePageBottomBarLabelVisibility>
    val fadePlayback: StateFlow<Boolean>
    val fadePlaybackDuration: StateFlow<Float>

    val requireAudioFocus: StateFlow<Boolean>
    val ignoreAudioFocusLoss: StateFlow<Boolean>
    val playOnHeadphonesConnect: StateFlow<Boolean>

    val pauseOnHeadphonesDisconnect: StateFlow<Boolean>
    val fastRewindDuration: StateFlow<Int>
    val fastForwardDuration: StateFlow<Int>

    val miniPlayerShowTrackControls: StateFlow<Boolean>
    val miniPlayerShowSeekControls: StateFlow<Boolean>
    val miniPlayerTextMarquee: StateFlow<Boolean>

    val nowPlayingLyricsLayout: StateFlow<NowPlayingLyricsLayout>
    val showNowPlayingAudioInformation: StateFlow<Boolean>
    val showNowPlayingSeekControls: StateFlow<Boolean>

    val currentPlaybackSpeed: StateFlow<Float>
    val currentPlaybackPitch: StateFlow<Float>
    val currentLoopMode: StateFlow<LoopMode>

    val shuffle: StateFlow<Boolean>
    val showLyrics: StateFlow<Boolean>
    val controlsLayoutIsDefault: StateFlow<Boolean>

    val currentlyDisabledTreePaths: StateFlow<List<String>>

    val sortSongsBy: StateFlow<SortSongsBy>
    val sortSongsInReverse: StateFlow<Boolean>

    val sortArtistsBy: StateFlow<SortArtistsBy>
    val sortArtistsInReverse: StateFlow<Boolean>

    val sortGenresBy: StateFlow<SortGenresBy>
    val sortGenresInReverse: StateFlow<Boolean>

    val sortPlaylistsBy: StateFlow<SortPlaylistsBy>
    val sortPlaylistsInReverse: StateFlow<Boolean>

    val sortAlbumsBy: StateFlow<SortAlbumsBy>
    val sortAlbumsInReverse: StateFlow<Boolean>

    val sortPathsBy: StateFlow<SortPathsBy>
    val sortPathsInReverse: StateFlow<Boolean>

    val currentlyPlayingSongId: StateFlow<String>

    suspend fun setLanguage( localeCode: String )
    suspend fun setFont( fontName: String )
    suspend fun setFontScale( fontScale: Float )

    suspend fun setThemeMode( themeMode: ThemeMode )
    suspend fun setUseMaterialYou( useMaterialYou: Boolean )
    suspend fun setPrimaryColorName( primaryColorName: String )

    suspend fun setHomePageBottomBarLabelVisibility( homePageBottomBarLabelVisibility: HomePageBottomBarLabelVisibility)
    suspend fun setFadePlayback( fadePlayback: Boolean )
    suspend fun setFadePlaybackDuration( fadePlaybackDuration: Float )

    suspend fun setRequireAudioFocus( requireAudioFocus: Boolean )
    suspend fun setIgnoreAudioFocusLoss( ignoreAudioFocusLoss: Boolean )
    suspend fun setPlayOnHeadphonesConnect( playOnHeadphonesConnect: Boolean )

    suspend fun setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect: Boolean )
    suspend fun setFastRewindDuration( fastRewindDuration: Int )
    suspend fun setFastForwardDuration( fastForwardDuration: Int )

    suspend fun setMiniPlayerShowTrackControls( showTrackControls: Boolean )
    suspend fun setMiniPlayerShowSeekControls( showSeekControls: Boolean )
    suspend fun setMiniPlayerTextMarquee( textMarquee: Boolean )

    suspend fun setNowPlayingLyricsLayout( nowPlayingLyricsLayout: NowPlayingLyricsLayout)
    suspend fun setShowNowPlayingAudioInformation( showNowPlayingAudioInformation: Boolean )
    suspend fun setShowNowPlayingSeekControls( showNowPlayingSeekControls: Boolean )

    suspend fun setCurrentPlayingSpeed( currentPlayingSpeed: Float )
    suspend fun setCurrentPlayingPitch( currentPlayingPitch: Float )
    suspend fun setCurrentLoopMode( currentLoopMode: LoopMode)

    suspend fun setShuffle( shuffle: Boolean )
    suspend fun setShowLyrics( showLyrics: Boolean )
    suspend fun setControlsLayoutIsDefault( controlsLayoutIsDefault: Boolean )

    suspend fun setCurrentlyDisabledTreePaths( paths: List<String> )

    suspend fun setSortSongsBy(newSortSongsBy: SortSongsBy )
    suspend fun setSortSongsInReverse( sortSongsInReverse: Boolean )

    suspend fun setSortArtistsBy( value: SortArtistsBy )
    suspend fun setSortArtistsInReverseTo( sortArtistsInReverse: Boolean )

    suspend fun setSortGenresBy( sortGenresBy: SortGenresBy )
    suspend fun setSortGenresInReverse( sortGenresInReverse: Boolean )

    suspend fun setSortPlaylistsBy( sortPlaylistsBy: SortPlaylistsBy )
    suspend fun setSortPlaylistsInReverse( sortPlaylistsInReverse: Boolean )

    suspend fun setSortAlbumsBy( sortAlbumsBy: SortAlbumsBy )
    suspend fun setSortAlbumsInReverse( sortAlbumsInReverse: Boolean )

    suspend fun setSortPathsBy( sortPathsBy: SortPathsBy )
    suspend fun setSortPathsInReverse( reverse: Boolean )

    suspend fun saveCurrentlyPlayingSongId( songId: String )

}