package com.squad.musicmatters.ui.settings

import com.squad.musicmatters.MainDispatcherRule
import com.squad.musicmatters.core.data.preferences.HomePageBottomBarLabelVisibility
import com.squad.musicmatters.core.data.preferences.NowPlayingLyricsLayout
import com.squad.musicmatters.core.data.preferences.impl.SettingsDefaults
import com.squad.musicmatters.core.data.settings.SettingsRepository
import com.squad.musicmatters.core.data.settings.impl.scalingPresets
import com.squad.musicmatters.core.designsystem.theme.MusicMattersFont
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTypography
import com.squad.musicmatters.core.designsystem.theme.PrimaryThemeColors
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.i8n.Belarusian
import com.squad.musicmatters.core.i8n.Chinese
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.French
import com.squad.musicmatters.core.i8n.German
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.i8n.Spanish
import com.squad.musicmatters.core.model.ThemeMode
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var settingsViewModel: SettingsViewModel

    @Before
    fun setup() {
        settingsRepository = FakeSettingsRepository()
        settingsViewModel = SettingsViewModel( settingsRepository )
    }

    @Test
    fun testLanguageChange() {
        assertEquals( "Settings", settingsViewModel.uiState.value.language.settings )
        changeLanguageTo( Belarusian, "Налады" )
        changeLanguageTo( Chinese, "设置" )
        changeLanguageTo( English, "Settings" )
        changeLanguageTo( French, "Paramètres" )
        changeLanguageTo( German, "Einstellungen" )
        changeLanguageTo( Spanish, "Configuración" )
    }

    private fun changeLanguageTo( language: Language, testString: String ) = runTest {
        settingsRepository.setLanguage( language.locale )
        val currentLanguage = settingsViewModel.uiState.value.language
        assertEquals( testString, currentLanguage.settings )
    }

    @Test
    fun testFontChange() = runTest {
        assertEquals( SupportedFonts.ProductSans.name, settingsViewModel.uiState.value.font.name )
        MusicMattersTypography.all.values.forEach { changeFontTo( it ) }
    }


    private fun changeFontTo( font: MusicMattersFont ) = runTest {
        settingsRepository.setFont( font.name )
        val currentFont = settingsViewModel.uiState.value.font
        assertEquals( font.name, currentFont.name )
    }

    @Test
    fun testFontScaleChange() = runTest {
        assertEquals( 1.0f, settingsViewModel.uiState.value.fontScale )
        scalingPresets.forEach { changeFontScaleTo( it )  }
    }

    private fun changeFontScaleTo( fontScale: Float ) = runTest {
        settingsRepository.setFontScale( fontScale )
        val currentFontScale = settingsViewModel.uiState.value.fontScale
        assertEquals( fontScale, currentFontScale )
    }

    @Test
    fun whenValidFontScaleStringIsProvided_settingsViewModelCorrectlyParsesIt() {
        settingsViewModel.setFontScale( "1.25" )
        assertEquals( "1.25", settingsViewModel.uiState.value.fontScale.toString() )
    }

    @Test
    fun whenInvalidFontScaleIsProvided_settingsViewModelCorrectlyIdentifiesIt() {
        settingsViewModel.setFontScale( "1245323" )
        assertEquals( "1.0", settingsViewModel.uiState.value.fontScale.toString() )
    }

    @Test
    fun testThemeModeChange() {
        assertEquals( ThemeMode.SYSTEM.name, settingsViewModel.uiState.value.themeMode.name )
        ThemeMode.entries.forEach { changeThemeModeTo( it ) }
    }

    private fun changeThemeModeTo( themeMode: ThemeMode ) = runTest {
        settingsRepository.setThemeMode( themeMode )
        assertEquals( themeMode.name, settingsViewModel.uiState.value.themeMode.name )
    }

    @Test
    fun testUseMaterialYouChange() {
        assertTrue( settingsViewModel.uiState.value.useMaterialYou )
        changeUseMaterialYouTo( true )
        changeUseMaterialYouTo( false )
    }

    private fun changeUseMaterialYouTo( useMaterialYou: Boolean ) = runTest {
        settingsRepository.setUseMaterialYou( useMaterialYou )
        assertEquals( useMaterialYou, settingsViewModel.uiState.value.useMaterialYou )
    }

    @Test
    fun testPrimaryColorNameChange() {
        assertEquals( SettingsDefaults.PRIMARY_COLOR_NAME, settingsViewModel.uiState.value.primaryColorName )
        PrimaryThemeColors.entries.forEach {
            changePrimaryColorTo( it.name )
        }
    }

    private fun changePrimaryColorTo( primaryColorName: String ) = runTest {
        settingsRepository.setPrimaryColorName( primaryColorName )
        assertEquals( primaryColorName, settingsViewModel.uiState.value.primaryColorName )
    }

    @Test
    fun testHomePageBottomBarLabelVisibilityChange() {
        assertEquals( HomePageBottomBarLabelVisibility.ALWAYS_VISIBLE,
            settingsViewModel.uiState.value.homePageBottomBarLabelVisibility )
        changeHomePageBottomBarLabelVisibilityTo( HomePageBottomBarLabelVisibility.ALWAYS_VISIBLE )
        changeHomePageBottomBarLabelVisibilityTo( HomePageBottomBarLabelVisibility.VISIBLE_WHEN_ACTIVE )
        changeHomePageBottomBarLabelVisibilityTo( HomePageBottomBarLabelVisibility.INVISIBLE )
    }

    private fun changeHomePageBottomBarLabelVisibilityTo(
        value: HomePageBottomBarLabelVisibility
    ) = runTest {
        settingsRepository.setHomePageBottomBarLabelVisibility( value )
        assertEquals( value, settingsViewModel.uiState.value.homePageBottomBarLabelVisibility )
    }

    @Test
    fun testFadePlaybackSettingChange() = runTest {
        assertFalse( settingsViewModel.uiState.value.fadePlayback )
        settingsRepository.setFadePlayback( true )
        assertTrue( settingsViewModel.uiState.value.fadePlayback )
        settingsRepository.setFadePlayback( false )
        assertFalse( settingsViewModel.uiState.value.fadePlayback )
    }

    @Test
    fun testFadePlaybackDurationChange() = runTest {
        assertEquals( SettingsDefaults.FADE_PLAYBACK_DURATION,
            settingsViewModel.uiState.value.fadePlaybackDuration )
        for ( duration in 1..100 ) {
            settingsRepository.setFadePlaybackDuration( duration.toFloat() )
            assertEquals( duration.toFloat(), settingsViewModel.uiState.value.fadePlaybackDuration )
        }
    }


    @Test
    fun testRequireAudioFocusSettingChange() = runTest {
        assertTrue( settingsViewModel.uiState.value.requireAudioFocus )
        settingsRepository.setRequireAudioFocus( false )
        assertFalse( settingsViewModel.uiState.value.requireAudioFocus )
        settingsRepository.setRequireAudioFocus( true )
        assertTrue( settingsViewModel.uiState.value.requireAudioFocus )
    }

    @Test
    fun testIgnoreAudioFocusLossSettingChange() = runTest {
        assertFalse( settingsViewModel.uiState.value.ignoreAudioFocusLoss )
        settingsRepository.setIgnoreAudioFocusLoss( true )
        assertTrue( settingsViewModel.uiState.value.ignoreAudioFocusLoss )
        settingsRepository.setIgnoreAudioFocusLoss( false )
        assertFalse( settingsViewModel.uiState.value.ignoreAudioFocusLoss )
    }

    @Test
    fun testPlayOnHeadphonesConnectSettingChange() = runTest {
        assertFalse( settingsViewModel.uiState.value.playOnHeadphonesConnect )
        settingsRepository.setPlayOnHeadphonesConnect( true )
        assertTrue( settingsViewModel.uiState.value.playOnHeadphonesConnect )
        settingsRepository.setPlayOnHeadphonesConnect( false )
        assertFalse( settingsViewModel.uiState.value.playOnHeadphonesConnect )
    }

    @Test
    fun testPauseOnHeadphonesDisconnectSettingChange() = runTest {
        assertTrue( settingsViewModel.uiState.value.pauseOnHeadphonesDisconnect )
        settingsRepository.setPauseOnHeadphonesDisconnect( false )
        assertFalse( settingsViewModel.uiState.value.pauseOnHeadphonesDisconnect )
        settingsRepository.setPauseOnHeadphonesDisconnect( true )
        assertTrue( settingsViewModel.uiState.value.pauseOnHeadphonesDisconnect )
    }

    @Test
    fun testFastRewindDurationSettingChange() = runTest {
        assertEquals( SettingsDefaults.FAST_REWIND_DURATION,
            settingsViewModel.uiState.value.fastRewindDuration )
        for ( duration in 3 .. 60 ) {
            settingsRepository.setFastRewindDuration( duration )
            assertEquals( duration, settingsViewModel.uiState.value.fastRewindDuration )
        }
    }

    @Test
    fun testFastForwardDurationSettingChange() = runTest {
        assertEquals( SettingsDefaults.FAST_FORWARD_DURATION,
            settingsViewModel.uiState.value.fastForwardDuration )
        for ( duration in 3 .. 60 ) {
            settingsRepository.setFastForwardDuration( duration )
            assertEquals( duration, settingsViewModel.uiState.value.fastForwardDuration )
        }
    }

    @Test
    fun testMiniPlayerShowTrackControlsSettingChange() = runTest {
        assertTrue( settingsViewModel.uiState.value.miniPlayerShowTrackControls )
        settingsRepository.setMiniPlayerShowTrackControls( false )
        assertFalse( settingsViewModel.uiState.value.miniPlayerShowTrackControls )
        settingsRepository.setMiniPlayerShowTrackControls( true )
        assertTrue( settingsViewModel.uiState.value.miniPlayerShowTrackControls )
    }

    @Test
    fun testMiniPlayerShowSeekControlsSettingChange() = runTest {
        assertFalse( settingsViewModel.uiState.value.miniPlayerShowSeekControls )
        settingsRepository.setMiniPlayerShowSeekControls( true )
        assertTrue( settingsViewModel.uiState.value.miniPlayerShowSeekControls )
        settingsRepository.setMiniPlayerShowSeekControls( false )
        assertFalse( settingsViewModel.uiState.value.miniPlayerShowSeekControls )
    }

    @Test
    fun testMiniPlayerTextMarqueeSettingChange() = runTest {
        assertTrue( settingsViewModel.uiState.value.miniPlayerTextMarquee )
        settingsRepository.setMiniPlayerTextMarquee( false )
        assertFalse( settingsViewModel.uiState.value.miniPlayerTextMarquee )
        settingsRepository.setMiniPlayerTextMarquee( true )
        assertTrue( settingsViewModel.uiState.value.miniPlayerTextMarquee )
    }

    @Test
    fun testNowPlayingControlsLayoutSettingChange() = runTest {
        assertEquals( SettingsDefaults.CONTROLS_LAYOUT_IS_DEFAULT,
            settingsViewModel.uiState.value.controlsLayoutIsDefault )
        listOf( true, false ).forEach {
            settingsRepository.setControlsLayoutIsDefault( it )
            assertEquals( it, settingsViewModel.uiState.value.controlsLayoutIsDefault )
        }
    }

    @Test
    fun testNowPlayingLyricsLayoutSettingChange() = runTest {
        assertEquals( SettingsDefaults.nowPlayingLyricsLayout,
            settingsViewModel.uiState.value.nowPlayingLyricsLayout )
        NowPlayingLyricsLayout.entries.forEach {
            settingsRepository.setNowPlayingLyricsLayout( it )
            assertEquals( it, settingsViewModel.uiState.value.nowPlayingLyricsLayout )
        }
    }

    @Test
    fun testShowNowPlayingAudioInformationSettingChange() = runTest {
        assertEquals( SettingsDefaults.SHOW_NOW_PLAYING_AUDIO_INFORMATION,
            settingsViewModel.uiState.value.showNowPlayingAudioInformation )
        listOf( true, false ).forEach {
            settingsRepository.setShowNowPlayingAudioInformation( it )
            assertEquals( it, settingsViewModel.uiState.value.showNowPlayingAudioInformation )
        }
    }

    @Test
    fun testShowNowPlayingSeekControlsSettingChange() = runTest {
        assertEquals( SettingsDefaults.SHOW_NOW_PLAYING_SEEK_CONTROLS,
            settingsViewModel.uiState.value.showNowPlayingSeekControls )
        listOf( true, false ).forEach {
            settingsRepository.setShowNowPlayingSeekControls( it )
            assertEquals( it, settingsViewModel.uiState.value.showNowPlayingSeekControls )
        }
    }

}