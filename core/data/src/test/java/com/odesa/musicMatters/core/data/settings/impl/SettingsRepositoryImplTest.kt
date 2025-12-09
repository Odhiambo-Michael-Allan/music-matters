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
import com.odesa.musicMatters.core.data.preferences.allowedSpeedPitchValues
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.datatesting.store.FakePreferencesStoreImpl
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersFont
import com.odesa.musicMatters.core.designsystem.theme.MusicallyTypography
import com.odesa.musicMatters.core.designsystem.theme.PrimaryThemeColors
import com.odesa.musicMatters.core.designsystem.theme.SupportedFonts
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.Belarusian
import com.odesa.musicMatters.core.i8n.Chinese
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.French
import com.odesa.musicMatters.core.i8n.German
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.i8n.Spanish
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith( RobolectricTestRunner::class )
class SettingsRepositoryImplTest {

    private lateinit var preferenceStore: PreferenceStore
    private lateinit var settingsRepository: SettingsRepository

    @Before
    fun setup() {
        preferenceStore = FakePreferencesStoreImpl()
        settingsRepository = SettingsRepositoryImpl( preferenceStore )
    }

    @Test
    fun testLanguageChange() {
        TestCase.assertEquals("Settings", settingsRepository.language.value.settings)
        changeLanguageTo( Belarusian, "Налады" )
        changeLanguageTo( Chinese, "设置" )
        changeLanguageTo( English, "Settings" )
        changeLanguageTo( French, "Paramètres" )
        changeLanguageTo( German, "Einstellungen" )
        changeLanguageTo( Spanish, "Configuración" )
    }

    private fun changeLanguageTo(language: Language, testString: String ) = runTest {
        settingsRepository.setLanguage( language.locale )
        TestCase.assertEquals( language.locale, preferenceStore.getLanguage() )
        val currentLanguage = settingsRepository.language.value
        TestCase.assertEquals( testString, currentLanguage.settings )
    }

    @Test
    fun testFontChange() {
        TestCase.assertEquals(SupportedFonts.ProductSans.name, settingsRepository.font.value.name)
        MusicallyTypography.all.values.forEach { changeFontTo( it ) }
    }

    private fun changeFontTo( font: MusicMattersFont) = runTest {
        settingsRepository.setFont( font.name )
        TestCase.assertEquals( font.name, preferenceStore.getFontName() )
        val currentFont = settingsRepository.font.value
        TestCase.assertEquals( font.name, currentFont.name )
    }


    @Test
    fun testFontScaleChange() {
        TestCase.assertEquals(SettingsDefaults.FONT_SCALE, settingsRepository.fontScale.value)
        scalingPresets.forEach { changeFontScaleTo( it ) }
    }

    private fun changeFontScaleTo( fontScale: Float ) = runTest {
        settingsRepository.setFontScale( fontScale )
        TestCase.assertEquals( fontScale, preferenceStore.getFontScale() )
        TestCase.assertEquals( fontScale, settingsRepository.fontScale.value )
    }

    @Test
    fun testThemeModeChange() {
        TestCase.assertEquals(ThemeMode.SYSTEM.name, settingsRepository.themeMode.value.name)
        ThemeMode.entries.forEach { changeThemeModeTo( it ) }
    }

    private fun changeThemeModeTo( themeMode: ThemeMode) = runTest {
        settingsRepository.setThemeMode( themeMode )
        TestCase.assertEquals( themeMode, preferenceStore.getThemeMode() )
        TestCase.assertEquals( themeMode, settingsRepository.themeMode.value )
    }

    @Test
    fun testUseMaterialYouChange() {
        TestCase.assertTrue(settingsRepository.useMaterialYou.value)
        changeUseMaterialYouTo( true )
        changeUseMaterialYouTo( false )
    }
    private fun changeUseMaterialYouTo( useMaterialYou: Boolean ) = runTest {
        settingsRepository.setUseMaterialYou( useMaterialYou )
        TestCase.assertEquals( useMaterialYou, preferenceStore.getUseMaterialYou() )
        TestCase.assertEquals( useMaterialYou, settingsRepository.useMaterialYou.value )
    }

    @Test
    fun testPrimaryColorChange() {
        TestCase.assertEquals(
            SettingsDefaults.PRIMARY_COLOR_NAME,
            settingsRepository.primaryColorName.value
        )
        PrimaryThemeColors.entries.forEach {
            changePrimaryColorTo( it.name )
        }
    }

    private fun changePrimaryColorTo( primaryColorName: String ) = runTest {
        settingsRepository.setPrimaryColorName( primaryColorName )
        TestCase.assertEquals( primaryColorName, preferenceStore.getPrimaryColorName() )
        TestCase.assertEquals( primaryColorName, settingsRepository.primaryColorName.value )
    }

    @Test
    fun testHomePageBottomBarLabelVisibilityChange() {
        TestCase.assertEquals(
            HomePageBottomBarLabelVisibility.ALWAYS_VISIBLE,
            settingsRepository.homePageBottomBarLabelVisibility.value
        )
        changeHomePageBottomBarLabelVisibilityTo( HomePageBottomBarLabelVisibility.ALWAYS_VISIBLE )
        changeHomePageBottomBarLabelVisibilityTo( HomePageBottomBarLabelVisibility.VISIBLE_WHEN_ACTIVE )
        changeHomePageBottomBarLabelVisibilityTo( HomePageBottomBarLabelVisibility.INVISIBLE )
    }

    private fun changeHomePageBottomBarLabelVisibilityTo(
        value: HomePageBottomBarLabelVisibility
    ) = runTest {
        settingsRepository.setHomePageBottomBarLabelVisibility( value )
        TestCase.assertEquals( value, preferenceStore.getHomePageBottomBarLabelVisibility() )
        TestCase.assertEquals( value, settingsRepository.homePageBottomBarLabelVisibility.value )
    }

    @Test
    fun testFadePlaybackSettingChange() = runTest {
        TestCase.assertFalse( settingsRepository.fadePlayback.value )
        settingsRepository.setFadePlayback( true )
        TestCase.assertTrue( preferenceStore.getFadePlayback() )
        TestCase.assertTrue( settingsRepository.fadePlayback.value )
        settingsRepository.setFadePlayback( false )
        TestCase.assertFalse( preferenceStore.getFadePlayback() )
        TestCase.assertFalse( settingsRepository.fadePlayback.value )
    }

    @Test
    fun testFadePlaybackDurationChange() = runTest {
        TestCase.assertEquals( SettingsDefaults.FADE_PLAYBACK_DURATION,
            settingsRepository.fadePlaybackDuration.value )
        for ( duration in 1..100 ) {
            settingsRepository.setFadePlaybackDuration( duration.toFloat() )
            TestCase.assertEquals( duration.toFloat(), preferenceStore.getFadePlaybackDuration() )
            TestCase.assertEquals( duration.toFloat(), settingsRepository.fadePlaybackDuration.value )
        }
    }

    @Test
    fun testRequireAudioFocusSettingChange() = runTest {
        TestCase.assertTrue( settingsRepository.requireAudioFocus.value )
        settingsRepository.setRequireAudioFocus( false )
        TestCase.assertFalse( preferenceStore.getRequireAudioFocus() )
        TestCase.assertFalse( settingsRepository.requireAudioFocus.value )
        settingsRepository.setRequireAudioFocus( true )
        TestCase.assertTrue( preferenceStore.getRequireAudioFocus() )
        TestCase.assertTrue( settingsRepository.requireAudioFocus.value )
    }

    @Test
    fun testIgnoreAudioFocusLossSettingChange() = runTest {
        TestCase.assertFalse( settingsRepository.ignoreAudioFocusLoss.value )
        settingsRepository.setIgnoreAudioFocusLoss( false )
        TestCase.assertFalse( preferenceStore.getIgnoreAudioFocusLoss() )
        TestCase.assertFalse( settingsRepository.ignoreAudioFocusLoss.value )
        settingsRepository.setIgnoreAudioFocusLoss( true )
        TestCase.assertTrue( preferenceStore.getIgnoreAudioFocusLoss() )
        TestCase.assertTrue( settingsRepository.ignoreAudioFocusLoss.value )
    }

    @Test
    fun testPlayOnHeadphonesConnectSettingChange() = runTest {
        TestCase.assertFalse( settingsRepository.playOnHeadphonesConnect.value )
        settingsRepository.setPlayOnHeadphonesConnect( true )
        TestCase.assertTrue( preferenceStore.getPlayOnHeadphonesConnect() )
        TestCase.assertTrue( settingsRepository.playOnHeadphonesConnect.value )
        settingsRepository.setPlayOnHeadphonesConnect( false )
        TestCase.assertFalse( preferenceStore.getPlayOnHeadphonesConnect() )
        TestCase.assertFalse( settingsRepository.playOnHeadphonesConnect.value )
    }

    @Test
    fun testPauseOnHeadphonesDisconnectSettingChange() = runTest {
        TestCase.assertTrue( settingsRepository.pauseOnHeadphonesDisconnect.value )
        settingsRepository.setPauseOnHeadphonesDisconnect( false )
        TestCase.assertFalse( preferenceStore.getPauseOnHeadphonesDisconnect() )
        TestCase.assertFalse( settingsRepository.pauseOnHeadphonesDisconnect.value )
        settingsRepository.setPauseOnHeadphonesDisconnect( true )
        TestCase.assertTrue( preferenceStore.getPauseOnHeadphonesDisconnect() )
        TestCase.assertTrue( settingsRepository.pauseOnHeadphonesDisconnect.value )
    }

    @Test
    fun testFastRewindDurationSettingChange() = runTest {
        TestCase.assertEquals( SettingsDefaults.FAST_REWIND_DURATION,
            settingsRepository.fastRewindDuration.value )
        for ( duration in 3..60 ) {
            settingsRepository.setFastRewindDuration( duration )
            TestCase.assertEquals( duration, preferenceStore.getFastRewindDuration() )
            TestCase.assertEquals( duration, settingsRepository.fastRewindDuration.value )
        }
    }

    @Test
    fun testFastForwardDurationSettingChange() = runTest {
        TestCase.assertEquals( SettingsDefaults.FAST_FORWARD_DURATION,
            settingsRepository.fastForwardDuration.value )
        for ( duration in 3..60 ) {
            settingsRepository.setFastForwardDuration( duration )
            TestCase.assertEquals( duration, preferenceStore.getFastForwardDuration() )
            TestCase.assertEquals( duration, settingsRepository.fastForwardDuration.value )
        }
    }

    @Test
    fun testMiniPlayerShowTrackControlsSettingChange() = runTest {
        TestCase.assertTrue( settingsRepository.miniPlayerShowTrackControls.value )
        settingsRepository.setMiniPlayerShowTrackControls( false )
        TestCase.assertFalse( preferenceStore.getMiniPlayerShowTrackControls() )
        TestCase.assertFalse( settingsRepository.miniPlayerShowTrackControls.value )
        settingsRepository.setMiniPlayerShowTrackControls( true )
        TestCase.assertTrue( preferenceStore.getMiniPlayerShowTrackControls() )
        TestCase.assertTrue( settingsRepository.miniPlayerShowTrackControls.value )
    }

    @Test
    fun testMiniPlayerShowSeekControlsSettingChange() = runTest {
        TestCase.assertFalse( settingsRepository.miniPlayerShowSeekControls.value )
        settingsRepository.setMiniPlayerShowSeekControls( true )
        TestCase.assertTrue( preferenceStore.getMiniPlayerShowSeekControls() )
        TestCase.assertTrue( settingsRepository.miniPlayerShowSeekControls.value )
        settingsRepository.setMiniPlayerShowSeekControls( false )
        TestCase.assertFalse( preferenceStore.getMiniPlayerShowSeekControls() )
        TestCase.assertFalse( settingsRepository.miniPlayerShowSeekControls.value )
    }

    @Test
    fun testMiniPlayerTextMarqueeSettingChange() = runTest {
        TestCase.assertTrue( settingsRepository.miniPlayerTextMarquee.value )
        settingsRepository.setMiniPlayerTextMarquee( false )
        TestCase.assertFalse( preferenceStore.getMiniPlayerTextMarquee() )
        TestCase.assertFalse( settingsRepository.miniPlayerTextMarquee.value )
        settingsRepository.setMiniPlayerTextMarquee( true )
        TestCase.assertTrue( preferenceStore.getMiniPlayerTextMarquee() )
        TestCase.assertTrue( settingsRepository.miniPlayerTextMarquee.value)
    }

    @Test
    fun testNowPlayingControlsLayoutSettingChange() = runTest {
        TestCase.assertEquals( SettingsDefaults.CONTROLS_LAYOUT_IS_DEFAULT,
            settingsRepository.controlsLayoutIsDefault.value )
        settingsRepository.setControlsLayoutIsDefault( false )
        TestCase.assertFalse( preferenceStore.getControlsLayoutIsDefault() )
        TestCase.assertFalse( settingsRepository.controlsLayoutIsDefault.value )
        settingsRepository.setControlsLayoutIsDefault( true )
        TestCase.assertTrue( preferenceStore.getControlsLayoutIsDefault() )
        TestCase.assertTrue( settingsRepository.controlsLayoutIsDefault.value )
    }

    @Test
    fun testNowPlayingLyricsLayoutSettingChange() = runTest {
        TestCase.assertEquals( SettingsDefaults.nowPlayingLyricsLayout,
            settingsRepository.nowPlayingLyricsLayout.value )
        NowPlayingLyricsLayout.entries.forEach {
            settingsRepository.setNowPlayingLyricsLayout( it )
            TestCase.assertEquals( it, preferenceStore.getNowPlayingLyricsLayout() )
            TestCase.assertEquals( it, settingsRepository.nowPlayingLyricsLayout.value )
        }
    }

    @Test
    fun testShowNowPlayingAudioInformationSettingChange() = runTest {
        TestCase.assertFalse( settingsRepository.showNowPlayingAudioInformation.value )
        listOf( true, false ).forEach {
            settingsRepository.setShowNowPlayingAudioInformation( it )
            TestCase.assertEquals( it, preferenceStore.getShowNowPlayingAudioInformation() )
            TestCase.assertEquals( it, settingsRepository.showNowPlayingAudioInformation.value )
        }
    }

    @Test
    fun testShowNowPlayingSeekControlsSettingChange() = runTest {
        TestCase.assertFalse( settingsRepository.showNowPlayingSeekControls.value )
        listOf( true, false ).forEach {
            settingsRepository.setShowNowPlayingSeekControls( it )
            TestCase.assertEquals( it, preferenceStore.getShowNowPlayingSeekControls() )
            TestCase.assertEquals( it, settingsRepository.showNowPlayingSeekControls.value )
        }
    }

    @Test
    fun testShowLyricsSettingChange() = runTest {
        TestCase.assertFalse( settingsRepository.showLyrics.value )
        listOf( true, false ).forEach {
            settingsRepository.setShowLyrics( it )
            TestCase.assertEquals( it, preferenceStore.getShowLyrics() )
            TestCase.assertEquals( it, settingsRepository.showLyrics.value )
        }
    }

    @Test
    fun testShuffleChange() = runTest {
        TestCase.assertFalse( settingsRepository.shuffle.value )
        listOf( true, false ).forEach {
            settingsRepository.setShuffle( it )
            TestCase.assertEquals( it, preferenceStore.getShuffle() )
            TestCase.assertEquals( it, settingsRepository.shuffle.value )
        }
    }

    @Test
    fun testLoopModeChange() = runTest {
        TestCase.assertEquals( SettingsDefaults.loopMode, settingsRepository.currentLoopMode.value )
        LoopMode.entries.forEach {
            settingsRepository.setCurrentLoopMode( it )
            TestCase.assertEquals( it, preferenceStore.getCurrentLoopMode() )
            TestCase.assertEquals( it, settingsRepository.currentLoopMode.value )
        }
    }

    @Test
    fun testCurrentPlayingSpeedChange() = runTest {
        TestCase.assertEquals( SettingsDefaults.CURRENT_PLAYING_SPEED,
            settingsRepository.currentPlaybackSpeed.value )
        allowedSpeedPitchValues.forEach {
            settingsRepository.setCurrentPlayingSpeed( it )
            TestCase.assertEquals( it, preferenceStore.getCurrentPlayingSpeed() )
            TestCase.assertEquals( it, settingsRepository.currentPlaybackSpeed.value )
        }
    }

    @Test
    fun testCurrentPlayingPitchChange() = runTest {
        TestCase.assertEquals( SettingsDefaults.CURRENT_PLAYING_PITCH,
            settingsRepository.currentPlaybackPitch.value )
        allowedSpeedPitchValues.forEach {
            settingsRepository.setCurrentPlayingPitch( it )
            TestCase.assertEquals( it, preferenceStore.getCurrentPlayingPitch() )
            TestCase.assertEquals( it, settingsRepository.currentPlaybackPitch.value )
        }
    }

    @Test
    fun testCurrentlyDisabledTreePathsChange() = runTest {
        TestCase.assertEquals( 0, settingsRepository.currentlyDisabledTreePaths.value.size )
        settingsRepository.setCurrentlyDisabledTreePaths(
            listOf( "path-1", "path-2", "path-3", "path-4", "path-5" )
        )
        TestCase.assertEquals( 5, preferenceStore.getCurrentlyDisabledTreePaths().size )
        TestCase.assertEquals( 5, settingsRepository.currentlyDisabledTreePaths.value.size )
    }

    @Test
    fun testSortSongsByValueIsSetCorrectly() = runTest {
        TestCase.assertEquals( SortSongsBy.TITLE, settingsRepository.sortSongsBy.value )
        SortSongsBy.entries.forEach {
            settingsRepository.setSortSongsBy( it )
            TestCase.assertEquals( it, preferenceStore.getSortSongsBy() )
            TestCase.assertEquals( it, settingsRepository.sortSongsBy.value )
        }
    }

    @Test
    fun testSortSongsInReverseIsCorrectlySet() = runTest {
        TestCase.assertFalse( settingsRepository.sortSongsInReverse.value )
        settingsRepository.setSortSongsInReverse( true )
        TestCase.assertTrue( preferenceStore.getSortSongsInReverse() )
        TestCase.assertTrue( settingsRepository.sortSongsInReverse.value )
        settingsRepository.setSortSongsInReverse( false )
        TestCase.assertFalse( preferenceStore.getSortSongsInReverse() )
        TestCase.assertFalse( settingsRepository.sortSongsInReverse.value )
    }

    @Test
    fun testSortArtistsByValueIsSetCorrectly() = runTest {
        TestCase.assertEquals( SortArtistsBy.ARTIST_NAME, settingsRepository.sortArtistsBy.value )
        SortArtistsBy.entries.forEach {
            settingsRepository.setSortArtistsBy( it )
            TestCase.assertEquals( it, preferenceStore.getSortArtistsBy() )
            TestCase.assertEquals( it, settingsRepository.sortArtistsBy.value )
        }
    }

    @Test
    fun testSortArtistsInReverseIsCorrectlySet() = runTest {
        TestCase.assertEquals(
            SettingsDefaults.SORT_ARTISTS_IN_REVERSE,
            settingsRepository.sortArtistsInReverse.value
        )
        listOf( true, false ).forEach {
            settingsRepository.setSortArtistsInReverseTo( it )
            TestCase.assertEquals( it, preferenceStore.getSortArtistsInReverse() )
            TestCase.assertEquals( it, settingsRepository.sortArtistsInReverse.value )
        }

    }

    @Test
    fun testSortGenresByValueIsSetCorrectly() = runTest {
        TestCase.assertEquals(
            SettingsDefaults.sortGenresBy,
            settingsRepository.sortGenresBy.value
        )
        SortGenresBy.entries.forEach {
            settingsRepository.setSortGenresBy( it )
            TestCase.assertEquals( it, preferenceStore.getSortGenresBy() )
            TestCase.assertEquals( it, settingsRepository.sortGenresBy.value )
        }
    }

    @Test
    fun testSortGenresInReverseIsSetCorrectly() = runTest {
        TestCase.assertEquals(
            SettingsDefaults.SORT_GENRES_IN_REVERSE,
            settingsRepository.sortGenresInReverse.value
        )
        listOf( true, false ).forEach {
            settingsRepository.setSortGenresInReverse( it )
            TestCase.assertEquals( it, preferenceStore.getSortGenresInReverse() )
            TestCase.assertEquals( it, settingsRepository.sortGenresInReverse.value )
        }
    }

    @Test
    fun testSortPlaylistsByValueIsSetCorrectly() = runTest {
        TestCase.assertEquals(
            SettingsDefaults.sortPlaylistsBy,
            settingsRepository.sortPlaylistsBy.value
        )
        SortPlaylistsBy.entries.forEach {
            settingsRepository.setSortPlaylistsBy( it )
            TestCase.assertEquals( it, preferenceStore.getSortPlaylistsBy() )
            TestCase.assertEquals( it, settingsRepository.sortPlaylistsBy.value )
        }
    }

    @Test
    fun testSortPlaylistsInReverseIsSetCorrectly() = runTest {
        TestCase.assertEquals(
            SettingsDefaults.SORT_PLAYLISTS_IN_REVERSE,
            settingsRepository.sortPlaylistsInReverse.value
        )
        listOf( true, false ).forEach {
            settingsRepository.setSortPlaylistsInReverse( it )
            TestCase.assertEquals( it, preferenceStore.getSortPlaylistsInReverse() )
            TestCase.assertEquals( it, settingsRepository.sortPlaylistsInReverse.value )
        }
    }

    @Test
    fun testSortAlbumsByValueIsSetCorrectly() = runTest {
        TestCase.assertEquals(
            SettingsDefaults.sortAlbumsBy,
            settingsRepository.sortAlbumsBy.value
        )
        SortAlbumsBy.entries.forEach {
            settingsRepository.setSortAlbumsBy( it )
            TestCase.assertEquals( it, preferenceStore.getSortAlbumsBy() )
            TestCase.assertEquals( it, settingsRepository.sortAlbumsBy.value )
        }
    }

    @Test
    fun testSortAlbumsInReverseIsSetCorrectly() = runTest {
        TestCase.assertEquals(
            SettingsDefaults.SORT_ALBUMS_IN_REVERSE,
            settingsRepository.sortAlbumsInReverse.value
        )
        listOf( true, false ).forEach {
            settingsRepository.setSortAlbumsInReverse( it )
            TestCase.assertEquals( it, preferenceStore.getSortAlbumsInReverse() )
            TestCase.assertEquals( it, settingsRepository.sortAlbumsInReverse.value )
        }
    }

    @Test
    fun testSortPathsByIsSetCorrectly() = runTest {
        TestCase.assertEquals(
            SettingsDefaults.sortPathsBy,
            settingsRepository.sortPathsBy.value
        )
        SortPathsBy.entries.forEach {
            settingsRepository.setSortPathsBy( it )
            TestCase.assertEquals( it, preferenceStore.getSortPathsBy() )
            TestCase.assertEquals( it, settingsRepository.sortPathsBy.value )
        }
    }

    @Test
    fun testSortPathsInReverseIsSetCorrectly() = runTest {
        TestCase.assertEquals(
            SettingsDefaults.SORT_PATHS_IN_REVERSE,
            settingsRepository.sortPathsInReverse.value
        )
        listOf( true, false ).forEach {
            settingsRepository.setSortPathsInReverse( it )
            TestCase.assertEquals( it, preferenceStore.getSortPathsInReverse() )
            TestCase.assertEquals( it, settingsRepository.sortPathsInReverse.value )
        }
    }

    @Test
    fun testCurrentlyPlayingSongIdIsSavedCorrectly() = runTest {
        TestCase.assertTrue( settingsRepository.currentlyPlayingSongId.value.isEmpty() )
        testSongs.map { it.id }.forEach { id ->
            settingsRepository.saveCurrentlyPlayingSongId( id )
            TestCase.assertEquals( id, preferenceStore.getCurrentlyPlayingSongId() )
            TestCase.assertEquals( id, settingsRepository.currentlyPlayingSongId.value )
        }
    }
}