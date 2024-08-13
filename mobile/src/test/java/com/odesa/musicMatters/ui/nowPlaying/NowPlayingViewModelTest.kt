package com.odesa.musicMatters.ui.nowPlaying

import androidx.media3.common.Player
import com.odesa.musicMatters.core.common.connection.PlaybackState
import com.odesa.musicMatters.core.common.media.extensions.toSong
import com.odesa.musicMatters.core.data.preferences.LoopMode
import com.odesa.musicMatters.core.data.preferences.allowedSpeedPitchValues
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.datatesting.connection.FakeMusicServiceConnection
import com.odesa.musicMatters.core.datatesting.playlist.FakePlaylistRepository
import com.odesa.musicMatters.core.datatesting.repository.FakeSettingsRepository
import com.odesa.musicMatters.core.datatesting.repository.FakeSongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.datatesting.songs.id1
import com.odesa.musicMatters.core.datatesting.songs.testSongMediaItemsForId
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.Belarusian
import com.odesa.musicMatters.core.i8n.Chinese
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.French
import com.odesa.musicMatters.core.i8n.German
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.i8n.Spanish
import com.odesa.musicMatters.ui.components.PlaybackPosition
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith( RobolectricTestRunner::class )
class NowPlayingViewModelTest {


    private lateinit var musicServiceConnection: FakeMusicServiceConnection
    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository

    @Before
    fun setup() {
        musicServiceConnection = FakeMusicServiceConnection()
        settingsRepository = FakeSettingsRepository()
        playlistRepository = FakePlaylistRepository()
        songsAdditionalMetadataRepository = FakeSongsAdditionalMetadataRepository()
        nowPlayingViewModel = NowPlayingViewModel(
            musicServiceConnection = musicServiceConnection,
            settingsRepository = settingsRepository,
            playlistRepository = playlistRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
        )
    }

    @Test
    fun testCurrentlyPlayingSongIsCorrectlyUpdated() {
        musicServiceConnection.setNowPlaying( testSongMediaItemsForId.first() )
        assertNotNull( nowPlayingViewModel.uiState.value.currentlyPlayingSong )
        assertEquals( id1, nowPlayingViewModel.uiState.value.currentlyPlayingSong.id )
    }

    @Test
    fun testPlaybackStateIsCorrectlyUpdated() {
        assertEquals(
            PlaybackPosition.zero.played,
            nowPlayingViewModel.uiState.value.playbackPosition.played
        )
        assertEquals(
            PlaybackPosition.zero.total,
            nowPlayingViewModel.uiState.value.playbackPosition.total
        )
        musicServiceConnection.setPlaybackState(
            PlaybackState( Player.STATE_READY, playWhenReady = true, duration = 30000L )
        )
        assertEquals(
            30000L,
            nowPlayingViewModel.uiState.value.playbackPosition.total
        )
    }

    @Test
    fun testUpdatePlaybackPositionIsCorrectlyUpdated() {
        assertFalse( nowPlayingViewModel.updatePlaybackPosition.value )
        musicServiceConnection.setPlaybackState(
            PlaybackState( Player.STATE_READY, playWhenReady = true, duration = 50000L )
        )
        assertTrue( nowPlayingViewModel.updatePlaybackPosition.value )
        musicServiceConnection.setPlaybackState( PlaybackState( Player.STATE_ENDED ) )
        assertFalse( nowPlayingViewModel.updatePlaybackPosition.value )
    }

    @Test
    fun testQueueSizeIsCorrectlyUpdated() {
        assertEquals( 0, nowPlayingViewModel.uiState.value.queueSize )
        musicServiceConnection.setMediaItems( testSongMediaItemsForId )
        assertEquals( 3, nowPlayingViewModel.uiState.value.queueSize )
    }

    @Test
    fun testCurrentlyPlayingSongIndexIsCorrectlyUpdated() {
        assertEquals( 0,
            nowPlayingViewModel.uiState.value.currentlyPlayingSongIndex )
        musicServiceConnection.setCurrentMediaItemIndex( 10 )
        assertEquals( 10,
            nowPlayingViewModel.uiState.value.currentlyPlayingSongIndex )
    }

    @Test
    fun testLanguageChange() {
        assertEquals( "Settings",
            nowPlayingViewModel.uiState.value.language.settings )
        changeLanguageTo( Belarusian, "Налады" )
        changeLanguageTo( Chinese, "设置" )
        changeLanguageTo( English, "Settings" )
        changeLanguageTo( French, "Paramètres" )
        changeLanguageTo( German, "Einstellungen" )
        changeLanguageTo( Spanish, "Configuración" )
    }

    private fun changeLanguageTo( language: Language, testString: String ) = runTest {
        settingsRepository.setLanguage( language.locale )
        val currentLanguage = nowPlayingViewModel.uiState.value.language
        assertEquals( testString, currentLanguage.settings )
    }

    @Test
    fun testIsPlayingChange() {
        assertFalse( nowPlayingViewModel.uiState.value.isPlaying )
        musicServiceConnection.setIsPlaying( true )
        assertTrue( nowPlayingViewModel.uiState.value.isPlaying )
    }

    @Test
    fun testThemeModeChange() = runTest {
        assertEquals( SettingsDefaults.themeMode,
            nowPlayingViewModel.uiState.value.themeMode )
        ThemeMode.entries.forEach {
            settingsRepository.setThemeMode( it )
            assertEquals( it, nowPlayingViewModel.uiState.value.themeMode )
        }
    }

    @Test
    fun testTextMarqueeChange() = runTest {
        assertEquals( SettingsDefaults.MINI_PLAYER_TEXT_MARQUEE,
            nowPlayingViewModel.uiState.value.textMarquee )
        settingsRepository.setMiniPlayerTextMarquee( false )
        assertFalse( nowPlayingViewModel.uiState.value.textMarquee )
        settingsRepository.setMiniPlayerTextMarquee( true )
        assertTrue( nowPlayingViewModel.uiState.value.textMarquee )
    }

    @Test
    fun testShowTrackControlsChange() = runTest {
        assertEquals( SettingsDefaults.MINI_PLAYER_SHOW_TRACK_CONTROLS,
            nowPlayingViewModel.uiState.value.miniPlayerShowTrackControls )
        settingsRepository.setMiniPlayerShowTrackControls( false )
        assertFalse( nowPlayingViewModel.uiState.value.miniPlayerShowTrackControls )
        settingsRepository.setMiniPlayerShowTrackControls( true )
        assertTrue( nowPlayingViewModel.uiState.value.miniPlayerShowTrackControls )
    }

    @Test
    fun testShowSeekControlsChange() = runTest {
        assertEquals( SettingsDefaults.MINI_PLAYERS_SHOW_SEEK_CONTROLS,
            nowPlayingViewModel.uiState.value.miniPlayerShowSeekControls )
        settingsRepository.setMiniPlayerShowSeekControls( true )
        assertTrue( nowPlayingViewModel.uiState.value.miniPlayerShowSeekControls )
        settingsRepository.setMiniPlayerShowSeekControls( false )
        assertFalse( nowPlayingViewModel.uiState.value.miniPlayerShowSeekControls )
    }

    @Test
    fun testControlsLayoutIsDefaultChange() = runTest {
        assertEquals( SettingsDefaults.CONTROLS_LAYOUT_IS_DEFAULT,
            nowPlayingViewModel.uiState.value.controlsLayoutIsDefault )
        listOf( true, false ).forEach {
            settingsRepository.setControlsLayoutIsDefault( it )
            assertEquals( it,
                nowPlayingViewModel.uiState.value.controlsLayoutIsDefault )
        }
    }

    @Test
    fun testShowLyricsChange() = runTest {
        assertEquals( SettingsDefaults.SHOW_LYRICS,
            nowPlayingViewModel.uiState.value.showLyrics )
        listOf( true, false ).forEach {
            settingsRepository.setShowLyrics( it )
            assertEquals( it,
                nowPlayingViewModel.uiState.value.showLyrics )
        }
    }

    @Test
    fun testShuffleChange() = runTest {
        assertEquals( SettingsDefaults.SHUFFLE,
            nowPlayingViewModel.uiState.value.shuffle )
        listOf( true, false ).forEach {
            settingsRepository.setShuffle( it )
            assertEquals( it,
                nowPlayingViewModel.uiState.value.shuffle )
        }
    }

    @Test
    fun testLoopModeChange() = runTest {
        assertEquals( SettingsDefaults.loopMode,
            nowPlayingViewModel.uiState.value.currentLoopMode )
        LoopMode.entries.forEach {
            settingsRepository.setCurrentLoopMode( it )
            assertEquals( it, nowPlayingViewModel.uiState.value.currentLoopMode )
        }
    }

    @Test
    fun testPlayingSpeedChange() = runTest {
        assertEquals( SettingsDefaults.CURRENT_PLAYING_SPEED,
            nowPlayingViewModel.uiState.value.currentPlayingSpeed )
        allowedSpeedPitchValues.forEach {
            settingsRepository.setCurrentPlayingSpeed( it )
            assertEquals( it,
                nowPlayingViewModel.uiState.value.currentPlayingSpeed )
        }
    }

    @Test
    fun testPlayingPitchChange() = runTest {
        assertEquals( SettingsDefaults.CURRENT_PLAYING_PITCH,
            nowPlayingViewModel.uiState.value.currentPlayingPitch )
        allowedSpeedPitchValues.forEach {
            settingsRepository.setCurrentPlayingPitch( it )
            assertEquals( it,
                nowPlayingViewModel.uiState.value.currentPlayingPitch )
        }
    }

    @Test
    fun testToggleCurrentLoopMode() {
        assertEquals( SettingsDefaults.loopMode, nowPlayingViewModel.uiState.value.currentLoopMode )
        nowPlayingViewModel.toggleLoopMode()
        assertEquals( LoopMode.Song, nowPlayingViewModel.uiState.value.currentLoopMode )
        nowPlayingViewModel.toggleLoopMode()
        assertEquals( LoopMode.Queue, nowPlayingViewModel.uiState.value.currentLoopMode )
        nowPlayingViewModel.toggleLoopMode()
        assertEquals( SettingsDefaults.loopMode, nowPlayingViewModel.uiState.value.currentLoopMode )
    }

    @Test
    fun testToggleShuffleMode() {
        assertEquals( SettingsDefaults.SHUFFLE, nowPlayingViewModel.uiState.value.shuffle )
        nowPlayingViewModel.toggleShuffleMode()
        assertEquals( !SettingsDefaults.SHUFFLE, nowPlayingViewModel.uiState.value.shuffle )
        nowPlayingViewModel.toggleShuffleMode()
        assertEquals( SettingsDefaults.SHUFFLE, nowPlayingViewModel.uiState.value.shuffle )
    }

    @Test
    fun testCurrentlyPlayingSongIsFavoriteChange() = runTest {
        val currentlyPlayingSongId = testSongMediaItemsForId.first().toSong( setOf() )
        musicServiceConnection.setNowPlaying( testSongMediaItemsForId.first() )
        playlistRepository.addToFavorites( currentlyPlayingSongId.id )
        assertTrue( nowPlayingViewModel.uiState.value.currentlyPlayingSongIsFavorite )
        playlistRepository.removeFromFavorites( currentlyPlayingSongId.id )
        assertFalse( nowPlayingViewModel.uiState.value.currentlyPlayingSongIsFavorite )
    }

    @Test
    fun testShowNowPlayingAdditionalInfoChange() = runTest {
        assertFalse( nowPlayingViewModel.uiState.value.showSamplingInfo )
        settingsRepository.setShowNowPlayingAudioInformation( true )
        assertTrue( nowPlayingViewModel.uiState.value.showSamplingInfo )
        settingsRepository.setShowNowPlayingAudioInformation( false )
        assertFalse( nowPlayingViewModel.uiState.value.showSamplingInfo )
    }

}