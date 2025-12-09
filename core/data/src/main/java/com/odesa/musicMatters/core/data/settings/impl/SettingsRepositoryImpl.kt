package com.odesa.musicMatters.core.data.settings.impl

import com.odesa.musicMatters.core.data.preferences.HomePageBottomBarLabelVisibility
import com.odesa.musicMatters.core.data.preferences.LoopMode
import com.odesa.musicMatters.core.data.preferences.NowPlayingLyricsLayout
import com.odesa.musicMatters.core.data.preferences.PreferenceStore
import com.odesa.musicMatters.core.data.preferences.SortAlbumsBy
import com.odesa.musicMatters.core.data.preferences.SortArtistsBy
import com.odesa.musicMatters.core.data.preferences.SortGenresBy
import com.odesa.musicMatters.core.data.preferences.SortPathsBy
import com.odesa.musicMatters.core.data.preferences.SortPlaylistsBy
import com.odesa.musicMatters.core.data.preferences.SortSongsBy
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersFont
import com.odesa.musicMatters.core.designsystem.theme.SupportedFonts
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.Belarusian
import com.odesa.musicMatters.core.i8n.Chinese
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.French
import com.odesa.musicMatters.core.i8n.German
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.i8n.Spanish
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import timber.log.Timber

class SettingsRepositoryImpl( private val preferenceStore: PreferenceStore ) : SettingsRepository {

    private val _language = MutableStateFlow( getLanguage( preferenceStore.getLanguage() ) )
    override val language = _language.asStateFlow()

    private val _font = MutableStateFlow( getFont( preferenceStore.getFontName() ) )
    override val font = _font.asStateFlow()

    private val _fontScale = MutableStateFlow( preferenceStore.getFontScale() )
    override val fontScale = _fontScale.asStateFlow()

    private val _themeMode = MutableStateFlow( preferenceStore.getThemeMode() )
    override val themeMode = _themeMode.asStateFlow()

    private val _useMaterialYou = MutableStateFlow( preferenceStore.getUseMaterialYou() )
    override val useMaterialYou = _useMaterialYou.asStateFlow()

    private val _primaryColorName = MutableStateFlow( preferenceStore.getPrimaryColorName() )
    override val primaryColorName = _primaryColorName.asStateFlow()

    private val _homePageBottomBarLabelVisibility = MutableStateFlow( preferenceStore.getHomePageBottomBarLabelVisibility() )
    override val homePageBottomBarLabelVisibility = _homePageBottomBarLabelVisibility.asStateFlow()

    private val _fadePlayback = MutableStateFlow( preferenceStore.getFadePlayback() )
    override val fadePlayback = _fadePlayback.asStateFlow()

    private val _fadePlaybackDuration = MutableStateFlow( preferenceStore.getFadePlaybackDuration() )
    override val fadePlaybackDuration = _fadePlaybackDuration.asStateFlow()

    private val _requireAudioFocus = MutableStateFlow( preferenceStore.getRequireAudioFocus() )
    override val requireAudioFocus = _requireAudioFocus.asStateFlow()

    private val _ignoreAudioFocusLoss = MutableStateFlow( preferenceStore.getIgnoreAudioFocusLoss() )
    override val ignoreAudioFocusLoss = _ignoreAudioFocusLoss.asStateFlow()

    private val _playOnHeadphonesConnect = MutableStateFlow( preferenceStore.getPlayOnHeadphonesConnect() )
    override val playOnHeadphonesConnect = _playOnHeadphonesConnect.asStateFlow()

    private val _pauseOnHeadphonesDisconnect = MutableStateFlow( preferenceStore.getPauseOnHeadphonesDisconnect() )
    override val pauseOnHeadphonesDisconnect = _pauseOnHeadphonesDisconnect.asStateFlow()

    private val _fastRewindDuration = MutableStateFlow( preferenceStore.getFastRewindDuration() )
    override val fastRewindDuration = _fastRewindDuration.asStateFlow()

    private val _fastForwardDuration = MutableStateFlow( preferenceStore.getFastForwardDuration() )
    override val fastForwardDuration = _fastForwardDuration.asStateFlow()

    private val _miniPlayerShowTrackControls = MutableStateFlow( preferenceStore.getMiniPlayerShowTrackControls() )
    override val miniPlayerShowTrackControls = _miniPlayerShowTrackControls.asStateFlow()

    private val _miniPlayerShowSeekControls = MutableStateFlow( preferenceStore.getMiniPlayerShowSeekControls() )
    override val miniPlayerShowSeekControls = _miniPlayerShowSeekControls.asStateFlow()

    private val _miniPlayerTextMarquee = MutableStateFlow( preferenceStore.getMiniPlayerTextMarquee() )
    override val miniPlayerTextMarquee = _miniPlayerTextMarquee.asStateFlow()

    private val _nowPlayingLyricsLayout = MutableStateFlow( preferenceStore.getNowPlayingLyricsLayout() )
    override val nowPlayingLyricsLayout = _nowPlayingLyricsLayout.asStateFlow()

    private val _showNowPlayingAudioInformation = MutableStateFlow( preferenceStore.getShowNowPlayingAudioInformation() )
    override val showNowPlayingAudioInformation = _showNowPlayingAudioInformation.asStateFlow()

    private val _showNowPlayingSeekControls = MutableStateFlow( preferenceStore.getShowNowPlayingSeekControls() )
    override val showNowPlayingSeekControls = _showNowPlayingSeekControls.asStateFlow()

    private val _currentPlayingSpeed = MutableStateFlow( preferenceStore.getCurrentPlayingSpeed() )
    override val currentPlaybackSpeed = _currentPlayingSpeed.asStateFlow()

    private val _currentPlayingPitch = MutableStateFlow( preferenceStore.getCurrentPlayingPitch() )
    override val currentPlaybackPitch = _currentPlayingPitch.asStateFlow()

    private val _currentLoopMode = MutableStateFlow( preferenceStore.getCurrentLoopMode() )
    override val currentLoopMode = _currentLoopMode.asStateFlow()

    private val _shuffle = MutableStateFlow( preferenceStore.getShuffle() )
    override val shuffle = _shuffle.asStateFlow()

    private val _showLyrics = MutableStateFlow( preferenceStore.getShowLyrics() )
    override val showLyrics = _showLyrics.asStateFlow()

    private val _controlsLayoutIsDefault = MutableStateFlow( preferenceStore.getControlsLayoutIsDefault() )
    override val controlsLayoutIsDefault = _controlsLayoutIsDefault.asStateFlow()

    private val _currentlyDisabledTreePaths = MutableStateFlow( preferenceStore.getCurrentlyDisabledTreePaths() )
    override val currentlyDisabledTreePaths = _currentlyDisabledTreePaths.asStateFlow()

    private val _currentSortSongsBy = MutableStateFlow( preferenceStore.getSortSongsBy() )
    override val sortSongsBy = _currentSortSongsBy.asStateFlow()

    private val _sortSongsInReverse = MutableStateFlow( preferenceStore.getSortSongsInReverse() )
    override val sortSongsInReverse = _sortSongsInReverse.asStateFlow()

    private val _sortArtistsBy = MutableStateFlow( preferenceStore.getSortArtistsBy() )
    override val sortArtistsBy = _sortArtistsBy.asStateFlow()

    private val _sortArtistsInReverse = MutableStateFlow( preferenceStore.getSortArtistsInReverse() )
    override val sortArtistsInReverse = _sortArtistsInReverse.asStateFlow()

    private val _sortGenresBy = MutableStateFlow( preferenceStore.getSortGenresBy() )
    override val sortGenresBy = _sortGenresBy.asStateFlow()

    private val _sortGenresInReverse = MutableStateFlow( preferenceStore.getSortGenresInReverse() )
    override val sortGenresInReverse = _sortGenresInReverse.asStateFlow()

    private val _sortPlaylistsBy = MutableStateFlow( preferenceStore.getSortPlaylistsBy() )
    override val sortPlaylistsBy = _sortPlaylistsBy.asStateFlow()

    private val _sortPlaylistsInReverse = MutableStateFlow( preferenceStore.getSortPlaylistsInReverse() )
    override val sortPlaylistsInReverse = _sortPlaylistsInReverse.asStateFlow()

    private val _sortAlbumsBy = MutableStateFlow( preferenceStore.getSortAlbumsBy() )
    override val sortAlbumsBy = _sortAlbumsBy.asStateFlow()

    private val _sortAlbumsInReverse = MutableStateFlow( preferenceStore.getSortAlbumsInReverse() )
    override val sortAlbumsInReverse = _sortAlbumsInReverse.asStateFlow()

    private val _sortPathsBy = MutableStateFlow( preferenceStore.getSortPathsBy() )
    override val sortPathsBy = _sortPathsBy.asStateFlow()

    private val _sortPathsInReverse = MutableStateFlow( preferenceStore.getSortPathsInReverse() )
    override val sortPathsInReverse = _sortPathsInReverse.asStateFlow()

    private val _currentlyPlayingSongId = MutableStateFlow(
        preferenceStore.getCurrentlyPlayingSongId()
    )
    override val currentlyPlayingSongId = _currentlyPlayingSongId.asStateFlow()


    override suspend fun setLanguage( localeCode: String ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setLanguage( localeCode )
            _language.update {
                getLanguage( localeCode )
            }
        }
    }

    override suspend fun setFont( fontName: String ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setFontName( fontName )
            _font.update {
                getFont( fontName )
            }
        }
    }

    private fun getFont( fontName: String ): MusicMattersFont {
        return when( fontName ) {
            SupportedFonts.DMSans.name -> SupportedFonts.DMSans
            SupportedFonts.Inter.name -> SupportedFonts.Inter
            SupportedFonts.Poppins.name -> SupportedFonts.Poppins
            SupportedFonts.Roboto.name -> SupportedFonts.Roboto
            else -> SupportedFonts.ProductSans
        }
    }

    override suspend fun setFontScale( fontScale: Float ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setFontScale( fontScale )
            _fontScale.update {
                fontScale
            }
        }
    }


    override suspend fun setThemeMode( themeMode: ThemeMode ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setThemeMode( themeMode )
            _themeMode.update {
                themeMode
            }
        }
    }

    override suspend fun setUseMaterialYou( useMaterialYou: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setUseMaterialYou( useMaterialYou )
            _useMaterialYou.update {
                useMaterialYou
            }
        }
    }

    override suspend fun setPrimaryColorName( primaryColorName: String ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setPrimaryColorName( primaryColorName )
            _primaryColorName.update {
                primaryColorName
            }
        }
    }

    override suspend fun setHomePageBottomBarLabelVisibility(
        homePageBottomBarLabelVisibility: HomePageBottomBarLabelVisibility
    ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setHomePageBottomBarLabelVisibility( homePageBottomBarLabelVisibility )
            _homePageBottomBarLabelVisibility.update {
                homePageBottomBarLabelVisibility
            }
        }
    }

    override suspend fun setFadePlayback( fadePlayback: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setFadePlayback( fadePlayback )
            _fadePlayback.update {
                fadePlayback
            }
        }
    }

    override suspend fun setFadePlaybackDuration( fadePlaybackDuration: Float ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setFadePlaybackDuration( fadePlaybackDuration )
            _fadePlaybackDuration.update {
                fadePlaybackDuration
            }
        }
    }

    override suspend fun setRequireAudioFocus( requireAudioFocus: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setRequireAudioFocus( requireAudioFocus )
            _requireAudioFocus.update {
                requireAudioFocus
            }
        }
    }

    override suspend fun setIgnoreAudioFocusLoss( ignoreAudioFocusLoss: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setIgnoreAudioFocusLoss( ignoreAudioFocusLoss )
            _ignoreAudioFocusLoss.update {
                ignoreAudioFocusLoss
            }
        }
    }

    override suspend fun setPlayOnHeadphonesConnect( playOnHeadphonesConnect: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setPlayOnHeadphonesConnect( playOnHeadphonesConnect )
            _playOnHeadphonesConnect.update {
                playOnHeadphonesConnect
            }
        }
    }

    override suspend fun setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect )
            _pauseOnHeadphonesDisconnect.update {
                pauseOnHeadphonesDisconnect
            }
        }
    }

    override suspend fun setFastRewindDuration( fastRewindDuration: Int ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setFastRewindDuration( fastRewindDuration )
            _fastRewindDuration.update {
                fastRewindDuration
            }
        }
    }

    override suspend fun setFastForwardDuration( fastForwardDuration: Int ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setFastForwardDuration( fastForwardDuration )
            _fastForwardDuration.update {
                fastForwardDuration
            }
        }
    }

    override suspend fun setMiniPlayerShowTrackControls( showTrackControls: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setMiniPlayerShowTrackControls( showTrackControls )
            _miniPlayerShowTrackControls.update {
                showTrackControls
            }
        }
    }

    override suspend fun setMiniPlayerShowSeekControls( showSeekControls: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setMiniPlayerShowSeekControls( showSeekControls )
            _miniPlayerShowSeekControls.update {
                showSeekControls
            }
        }
    }

    override suspend fun setMiniPlayerTextMarquee( textMarquee: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setMiniPlayerTextMarquee( textMarquee )
            _miniPlayerTextMarquee.update {
                textMarquee
            }
        }
    }

    override suspend fun setNowPlayingLyricsLayout(
        nowPlayingLyricsLayout: NowPlayingLyricsLayout
    ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setNowPlayingLyricsLayout( nowPlayingLyricsLayout )
            _nowPlayingLyricsLayout.update {
                nowPlayingLyricsLayout
            }
        }
    }

    override suspend fun setShowNowPlayingAudioInformation(
        showNowPlayingAudioInformation: Boolean
    ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setShowNowPlayingAudioInformation( showNowPlayingAudioInformation )
            _showNowPlayingAudioInformation.update {
                showNowPlayingAudioInformation
            }
        }
    }

    override suspend fun setShowNowPlayingSeekControls( showNowPlayingSeekControls: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setShowNowPlayingSeekControls( showNowPlayingSeekControls )
            _showNowPlayingSeekControls.update {
                showNowPlayingSeekControls
            }
        }
    }

    override suspend fun setCurrentPlayingSpeed( currentPlayingSpeed: Float ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setCurrentPlayingSpeed( currentPlayingSpeed )
            _currentPlayingSpeed.update {
                currentPlayingSpeed
            }
        }
    }

    override suspend fun setCurrentPlayingPitch( currentPlayingPitch: Float ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setCurrentPlayingPitch( currentPlayingPitch )
            _currentPlayingPitch.update {
                currentPlayingPitch
            }
        }
    }

    override suspend fun setCurrentLoopMode( currentLoopMode: LoopMode ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setCurrentLoopMode( currentLoopMode )
            _currentLoopMode.update {
                currentLoopMode
            }
        }
    }

    override suspend fun setShuffle( shuffle: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setShuffle( shuffle )
            _shuffle.update {
                shuffle
            }
        }
    }

    override suspend fun setShowLyrics( showLyrics: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setShowLyrics( showLyrics )
            _showLyrics.update {
                showLyrics
            }
        }
    }

    override suspend fun setControlsLayoutIsDefault( controlsLayoutIsDefault: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setControlsLayoutIsDefault( controlsLayoutIsDefault )
            _controlsLayoutIsDefault.update {
                controlsLayoutIsDefault
            }
        }
    }

    override suspend fun setCurrentlyDisabledTreePaths( paths: List<String> ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setCurrentlyDisabledTreePaths( paths )
            _currentlyDisabledTreePaths.update {
                paths
            }
        }
    }

    override suspend fun setSortSongsBy( newSortSongsBy: SortSongsBy ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setSortSongsBy( newSortSongsBy )
            _currentSortSongsBy.value = newSortSongsBy
        }
    }

    override suspend fun setSortSongsInReverse( sortSongsInReverse: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setSortSongsInReverse( sortSongsInReverse )
            _sortSongsInReverse.value = sortSongsInReverse
        }
    }

    override suspend fun setSortArtistsBy( value: SortArtistsBy ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setSortArtistsBy( value )
            _sortArtistsBy.value = value
        }
    }

    override suspend fun setSortArtistsInReverseTo( sortArtistsInReverse: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setSortArtistsInReverse( sortArtistsInReverse )
            _sortArtistsInReverse.value = sortArtistsInReverse
        }
    }

    override suspend fun setSortGenresBy( sortGenresBy: SortGenresBy ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setSortGenresBy( sortGenresBy )
            _sortGenresBy.value = sortGenresBy
        }
    }

    override suspend fun setSortGenresInReverse( sortGenresInReverse: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setSortGenresInReverse( sortGenresInReverse )
            _sortGenresInReverse.value = sortGenresInReverse
        }
    }

    override suspend fun setSortPlaylistsBy( sortPlaylistsBy: SortPlaylistsBy ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setSortPlaylistsBy( sortPlaylistsBy )
            _sortPlaylistsBy.value = sortPlaylistsBy
        }
    }

    override suspend fun setSortPlaylistsInReverse( sortPlaylistsInReverse: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setSortPlaylistsInReverse( sortPlaylistsInReverse )
            _sortPlaylistsInReverse.value = sortPlaylistsInReverse
        }
    }

    override suspend fun setSortAlbumsBy( sortAlbumsBy: SortAlbumsBy ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setSortAlbumsBy( sortAlbumsBy )
            _sortAlbumsBy.value = sortAlbumsBy
        }
    }

    override suspend fun setSortAlbumsInReverse( sortAlbumsInReverse: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setSortAlbumsInReverse( sortAlbumsInReverse )
            _sortAlbumsInReverse.value = sortAlbumsInReverse
        }
    }

    override suspend fun setSortPathsBy( sortPathsBy: SortPathsBy ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setSortPathsBy( sortPathsBy )
            _sortPathsBy.value = sortPathsBy
        }
    }

    override suspend fun setSortPathsInReverse( reverse: Boolean ) {
        withContext( Dispatchers.IO ) {
            preferenceStore.setSortPathsInReverse( reverse )
            _sortPathsInReverse.value = reverse
        }
    }

    override suspend fun saveCurrentlyPlayingSongId( songId: String ) {
        withContext( Dispatchers.IO ) {
            Timber.tag( SETTINGS_REPOSITORY_TAG ).d( "SAVING CURRENTLY PLAYING SONG ID: $songId" )
            preferenceStore.setCurrentlyPlayingSongId( songId )
            _currentlyPlayingSongId.value = songId
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: SettingsRepository? = null

        fun getInstance( preferenceStore: PreferenceStore ): SettingsRepository {
            return INSTANCE ?: synchronized( this ) {
                SettingsRepositoryImpl( preferenceStore ).also { INSTANCE = it }
            }
        }
    }
}

fun getLanguage( localeCode: String ) : Language {
    return when ( localeCode ) {
        Belarusian.locale -> Belarusian
        Chinese.locale -> Chinese
        French.locale -> French
        German.locale -> German
        Spanish.locale -> Spanish
        else -> English
    }
}

val scalingPresets = listOf(
    0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f,
    1.75f, 2f, 2.25f, 2.5f, 2.75f, 3f
)

private const val SETTINGS_REPOSITORY_TAG = "SETTINGS-REPOSITORY-TAG"