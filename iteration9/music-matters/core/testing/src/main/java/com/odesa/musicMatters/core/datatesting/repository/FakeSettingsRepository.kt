package com.odesa.musicMatters.core.datatesting.repository


import com.odesa.musicMatters.core.data.preferences.HomePageBottomBarLabelVisibility
import com.odesa.musicMatters.core.data.preferences.LoopMode
import com.odesa.musicMatters.core.data.preferences.NowPlayingLyricsLayout
import com.odesa.musicMatters.core.data.preferences.SortAlbumsBy
import com.odesa.musicMatters.core.data.preferences.SortArtistsBy
import com.odesa.musicMatters.core.data.preferences.SortGenresBy
import com.odesa.musicMatters.core.data.preferences.SortPathsBy
import com.odesa.musicMatters.core.data.preferences.SortPlaylistsBy
import com.odesa.musicMatters.core.data.preferences.SortSongsBy
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.designsystem.theme.SupportedFonts
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.Belarusian
import com.odesa.musicMatters.core.i8n.Chinese
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.French
import com.odesa.musicMatters.core.i8n.German
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.i8n.Spanish
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingsRepository : SettingsRepository {

    private val _currentLanguage: MutableStateFlow<Language> = MutableStateFlow( English )
    override val language = _currentLanguage.asStateFlow()

    private val _currentFont = MutableStateFlow( SupportedFonts.ProductSans )
    override val font = _currentFont.asStateFlow()

    private val _fontScale = MutableStateFlow( 1.0f )
    override val fontScale = _fontScale.asStateFlow()

    private val _themeMode = MutableStateFlow( ThemeMode.SYSTEM )
    override val themeMode = _themeMode.asStateFlow()

    private val _useMaterialYou = MutableStateFlow( true )
    override val useMaterialYou = _useMaterialYou.asStateFlow()

    private val _primaryColorName = MutableStateFlow( SettingsDefaults.PRIMARY_COLOR_NAME )
    override val primaryColorName = _primaryColorName.asStateFlow()

    private val _homePageBottomBarLabelVisibility = MutableStateFlow(
        SettingsDefaults.homePageBottomBarLabelVisibility
    )
    override val homePageBottomBarLabelVisibility = _homePageBottomBarLabelVisibility.asStateFlow()

    private val _fadePlayback = MutableStateFlow( SettingsDefaults.FADE_PLAYBACK )
    override val fadePlayback = _fadePlayback.asStateFlow()

    private val _fadePlaybackDuration = MutableStateFlow( SettingsDefaults.FADE_PLAYBACK_DURATION )
    override val fadePlaybackDuration = _fadePlaybackDuration.asStateFlow()

    private val _requireAudioFocus = MutableStateFlow( SettingsDefaults.REQUIRE_AUDIO_FOCUS )
    override val requireAudioFocus = _requireAudioFocus.asStateFlow()

    private val _ignoreAudioFocusLoss = MutableStateFlow( SettingsDefaults.IGNORE_AUDIO_FOCUS_LOSS )
    override val ignoreAudioFocusLoss = _ignoreAudioFocusLoss.asStateFlow()

    private val _playOnHeadphoneConnect = MutableStateFlow(
        SettingsDefaults.PLAY_ON_HEADPHONES_CONNECT
    )
    override val playOnHeadphonesConnect = _playOnHeadphoneConnect.asStateFlow()

    private val _pauseOnHeadphonesDisconnect = MutableStateFlow(
        SettingsDefaults.PAUSE_ON_HEADPHONES_DISCONNECT
    )
    override val pauseOnHeadphonesDisconnect = _pauseOnHeadphonesDisconnect.asStateFlow()

    private val _fastRewindDuration = MutableStateFlow( SettingsDefaults.FAST_REWIND_DURATION )
    override val fastRewindDuration = _fastRewindDuration.asStateFlow()

    private val _fastForwardDuration = MutableStateFlow( SettingsDefaults.FAST_FORWARD_DURATION )
    override val fastForwardDuration = _fastForwardDuration.asStateFlow()

    private val _miniPlayerShowTrackControls = MutableStateFlow(
        SettingsDefaults.MINI_PLAYER_SHOW_TRACK_CONTROLS
    )
    override val miniPlayerShowTrackControls = _miniPlayerShowTrackControls.asStateFlow()

    private val _miniPlayerShowSeekControls = MutableStateFlow(
        SettingsDefaults.MINI_PLAYERS_SHOW_SEEK_CONTROLS
    )
    override val miniPlayerShowSeekControls = _miniPlayerShowSeekControls.asStateFlow()

    private val _miniPlayerTextMarquee = MutableStateFlow( SettingsDefaults.MINI_PLAYER_TEXT_MARQUEE )
    override val miniPlayerTextMarquee = _miniPlayerTextMarquee.asStateFlow()

    private val _nowPlayingLyricsLayout = MutableStateFlow(
        SettingsDefaults.nowPlayingLyricsLayout
    )
    override val nowPlayingLyricsLayout = _nowPlayingLyricsLayout.asStateFlow()

    private val _showNowPlayingAudioInformation = MutableStateFlow(
        SettingsDefaults.SHOW_NOW_PLAYING_AUDIO_INFORMATION
    )
    override val showNowPlayingAudioInformation = _showNowPlayingAudioInformation.asStateFlow()

    private val _showNowPlayingSeekControls = MutableStateFlow(
        SettingsDefaults.SHOW_NOW_PLAYING_SEEK_CONTROLS
    )
    override val showNowPlayingSeekControls = _showNowPlayingSeekControls.asStateFlow()

    private val _currentPlayingSpeed = MutableStateFlow(
        SettingsDefaults.CURRENT_PLAYING_SPEED
    )
    override val currentPlaybackSpeed = _currentPlayingSpeed.asStateFlow()

    private val _currentPlayingPitch = MutableStateFlow(
        SettingsDefaults.CURRENT_PLAYING_PITCH
    )
    override val currentPlaybackPitch = _currentPlayingPitch.asStateFlow()

    private val _currentLoopMode = MutableStateFlow( SettingsDefaults.loopMode )
    override val currentLoopMode = _currentLoopMode.asStateFlow()

    private val _shuffle = MutableStateFlow( SettingsDefaults.SHUFFLE )
    override val shuffle = _shuffle.asStateFlow()

    private val _showLyrics = MutableStateFlow( SettingsDefaults.SHOW_LYRICS )
    override val showLyrics = _showLyrics.asStateFlow()

    private val _controlsLayoutIsDefault = MutableStateFlow(
        SettingsDefaults.CONTROLS_LAYOUT_IS_DEFAULT )
    override val controlsLayoutIsDefault = _controlsLayoutIsDefault.asStateFlow()

    private val _currentlyDisabledTreePaths = MutableStateFlow( emptyList<String>() )
    override val currentlyDisabledTreePaths = _currentlyDisabledTreePaths.asStateFlow()

    private val _currentSortSongsBy = MutableStateFlow( SettingsDefaults.sortSongsBy )
    override val sortSongsBy = _currentSortSongsBy.asStateFlow()

    private val _sortSongsInReverse = MutableStateFlow( SettingsDefaults.SORT_SONGS_IN_REVERSE )
    override val sortSongsInReverse = _sortSongsInReverse.asStateFlow()

    private val _sortArtistsBy = MutableStateFlow( SettingsDefaults.sortArtistsBy )
    override val sortArtistsBy = _sortArtistsBy.asStateFlow()

    private val _sortArtistsInReverse = MutableStateFlow( SettingsDefaults.SORT_ARTISTS_IN_REVERSE )
    override val sortArtistsInReverse = _sortArtistsInReverse.asStateFlow()

    private val _sortGenresBy = MutableStateFlow( SettingsDefaults.sortGenresBy )
    override val sortGenresBy = _sortGenresBy.asStateFlow()

    private val _sortGenresInReverse = MutableStateFlow( SettingsDefaults.SORT_GENRES_IN_REVERSE )
    override val sortGenresInReverse = _sortGenresInReverse.asStateFlow()

    private val _sortPlaylistsBy = MutableStateFlow( SettingsDefaults.sortPlaylistsBy )
    override val sortPlaylistsBy = _sortPlaylistsBy.asStateFlow()

    private val _sortPlaylistsInReverse = MutableStateFlow( SettingsDefaults.SORT_PLAYLISTS_IN_REVERSE )
    override val sortPlaylistsInReverse = _sortPlaylistsInReverse.asStateFlow()

    private val _sortAlbumsBy = MutableStateFlow( SettingsDefaults.sortAlbumsBy )
    override val sortAlbumsBy = _sortAlbumsBy.asStateFlow()

    private val _sortAlbumsInReverse = MutableStateFlow( SettingsDefaults.SORT_ALBUMS_IN_REVERSE )
    override val sortAlbumsInReverse = _sortAlbumsInReverse.asStateFlow()

    private val _sortPathsBy = MutableStateFlow( SettingsDefaults.sortPathsBy )
    override val sortPathsBy = _sortPathsBy.asStateFlow()

    private val _sortPathsInReverse = MutableStateFlow( SettingsDefaults.SORT_PATHS_IN_REVERSE )
    override val sortPathsInReverse = _sortPathsInReverse.asStateFlow()

    private val _currentlyPlayingSongId = MutableStateFlow( "" )
    override val currentlyPlayingSongId = _currentlyPlayingSongId.asStateFlow()


    override suspend fun setLanguage( localeCode: String ) {
        _currentLanguage.value = resolveLanguage( localeCode )
    }

    private fun resolveLanguage( language: String ) = when( language ) {
        Belarusian.locale -> Belarusian
        Chinese.locale -> Chinese
        French.locale -> French
        German.locale -> German
        Spanish.locale -> Spanish
        else -> English
    }

    override suspend fun setFont( fontName: String ) {
        _currentFont.value = resolveFont( fontName )
    }

    private fun resolveFont( fontName: String ) = when( fontName ) {
        SupportedFonts.DMSans.name -> SupportedFonts.DMSans
        SupportedFonts.Inter.name -> SupportedFonts.Inter
        SupportedFonts.Poppins.name -> SupportedFonts.Poppins
        SupportedFonts.Roboto.name -> SupportedFonts.Roboto
        else -> SupportedFonts.ProductSans
    }

    override suspend fun setFontScale( fontScale: Float ) {
        _fontScale.value = fontScale
    }

    override suspend fun setThemeMode( themeMode: ThemeMode ) {
        _themeMode.value = themeMode
    }

    override suspend fun setUseMaterialYou( useMaterialYou: Boolean ) {
        _useMaterialYou.value = useMaterialYou
    }

    override suspend fun setPrimaryColorName( primaryColorName: String ) {
        _primaryColorName.value = primaryColorName
    }

    override suspend fun setHomePageBottomBarLabelVisibility(
        homePageBottomBarLabelVisibility: HomePageBottomBarLabelVisibility
    ) {
        _homePageBottomBarLabelVisibility.value = homePageBottomBarLabelVisibility
    }

    override suspend fun setFadePlayback( fadePlayback: Boolean ) {
        _fadePlayback.value = fadePlayback
    }

    override suspend fun setFadePlaybackDuration( fadePlaybackDuration: Float ) {
        _fadePlaybackDuration.value = fadePlaybackDuration
    }

    override suspend fun setRequireAudioFocus( requireAudioFocus: Boolean ) {
        _requireAudioFocus.value = requireAudioFocus
    }

    override suspend fun setIgnoreAudioFocusLoss( ignoreAudioFocusLoss: Boolean ) {
        _ignoreAudioFocusLoss.value = ignoreAudioFocusLoss
    }

    override suspend fun setPlayOnHeadphonesConnect( playOnHeadphonesConnect: Boolean ) {
        _playOnHeadphoneConnect.value = playOnHeadphonesConnect
    }

    override suspend fun setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect: Boolean ) {
        _pauseOnHeadphonesDisconnect.value = pauseOnHeadphonesDisconnect
    }

    override suspend fun setFastRewindDuration( fastRewindDuration: Int ) {
        _fastRewindDuration.value = fastRewindDuration
    }

    override suspend  fun setFastForwardDuration( fastForwardDuration: Int ) {
        _fastForwardDuration.value = fastForwardDuration
    }

    override suspend fun setMiniPlayerShowTrackControls( showTrackControls: Boolean ) {
        _miniPlayerShowTrackControls.value = showTrackControls
    }

    override suspend fun setMiniPlayerShowSeekControls( showSeekControls: Boolean ) {
        _miniPlayerShowSeekControls.value = showSeekControls
    }

    override suspend fun setMiniPlayerTextMarquee( textMarquee: Boolean ) {
        _miniPlayerTextMarquee.value = textMarquee
    }

    override suspend fun setNowPlayingLyricsLayout(
        nowPlayingLyricsLayout: NowPlayingLyricsLayout
    ) {
        _nowPlayingLyricsLayout.value = nowPlayingLyricsLayout
    }

    override suspend fun setShowNowPlayingAudioInformation(
        showNowPlayingAudioInformation: Boolean
    ) {
        _showNowPlayingAudioInformation.value = showNowPlayingAudioInformation
    }

    override suspend fun setShowNowPlayingSeekControls(
        showNowPlayingSeekControls: Boolean
    ) {
        _showNowPlayingSeekControls.value = showNowPlayingSeekControls
    }

    override suspend fun setCurrentPlayingSpeed( currentPlayingSpeed: Float ) {
        _currentPlayingSpeed.value = currentPlayingSpeed
    }

    override suspend fun setCurrentPlayingPitch( currentPlayingPitch: Float ) {
        _currentPlayingPitch.value = currentPlayingPitch
    }

    override suspend fun setCurrentLoopMode( currentLoopMode: LoopMode ) {
        _currentLoopMode.value = currentLoopMode
    }

    override suspend fun setShuffle( shuffle: Boolean ) {
        _shuffle.value = shuffle
    }

    override suspend fun setShowLyrics( showLyrics: Boolean ) {
        _showLyrics.value = showLyrics
    }

    override suspend fun setControlsLayoutIsDefault( controlsLayoutIsDefault: Boolean ) {
        _controlsLayoutIsDefault.value = controlsLayoutIsDefault
    }

    override suspend fun setCurrentlyDisabledTreePaths( paths: List<String> ) {
        _currentlyDisabledTreePaths.value = paths
    }

    override suspend fun setSortSongsBy(newSortSongsBy: SortSongsBy ) {
        _currentSortSongsBy.value = newSortSongsBy
    }

    override suspend fun setSortSongsInReverse( sortSongsInReverse: Boolean ) {
        _sortSongsInReverse.value = sortSongsInReverse
    }

    override suspend fun setSortArtistsBy( value: SortArtistsBy ) {
        _sortArtistsBy.value = value
    }

    override suspend fun setSortArtistsInReverseTo( sortArtistsInReverse: Boolean ) {
        _sortArtistsInReverse.value = sortArtistsInReverse
    }

    override suspend fun setSortGenresBy( sortGenresBy: SortGenresBy ) {
        _sortGenresBy.value = sortGenresBy
    }

    override suspend fun setSortGenresInReverse( sortGenresInReverse: Boolean ) {
        _sortGenresInReverse.value = sortGenresInReverse
    }

    override suspend fun setSortPlaylistsBy( sortPlaylistsBy: SortPlaylistsBy ) {
        _sortPlaylistsBy.value = sortPlaylistsBy
    }

    override suspend fun setSortPlaylistsInReverse( sortPlaylistsInReverse: Boolean ) {
        _sortPlaylistsInReverse.value = sortPlaylistsInReverse
    }

    override suspend fun setSortAlbumsBy( sortAlbumsBy: SortAlbumsBy ) {
        _sortAlbumsBy.value = sortAlbumsBy
    }

    override suspend fun setSortAlbumsInReverse( sortAlbumsInReverse: Boolean ) {
        _sortAlbumsInReverse.value = sortAlbumsInReverse
    }

    override suspend fun setSortPathsBy( sortPathsBy: SortPathsBy ) {
        _sortPathsBy.value = sortPathsBy
    }

    override suspend fun setSortPathsInReverse( reverse: Boolean ) {
        _sortPathsInReverse.value = reverse
    }

    override suspend fun saveCurrentlyPlayingSongId( songId: String ) {
        _currentlyPlayingSongId.value = songId
    }
}