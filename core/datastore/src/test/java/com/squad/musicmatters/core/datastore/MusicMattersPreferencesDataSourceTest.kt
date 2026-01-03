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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class MusicMattersPreferencesDataSourceTest {

    private val testScope = TestScope( UnconfinedTestDispatcher() )
    private lateinit var subject: MusicMattersPreferencesDataSource

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    @Before
    fun setUp() {
        subject = MusicMattersPreferencesDataSource(
            userPreferencesDataStore = tmpFolder.testUserPreferencesDataStore(
                coroutineScope = testScope.backgroundScope
            )
        )
    }

    @Test
    fun testChangeFontName() = testScope.runTest {
        assertEquals( "Google Sans", subject.userData.map { it.fontName }.first() )
        listOf(
            "font name 1",
            "font name 2",
            "font name 3",
            "font name 4",
            "font name 5"
        ).forEach {
            subject.setFontName( it )
            assertEquals( it, subject.userData.map { it.fontName }.first() )
        }
    }

    @Test
    fun testChangeFontScale() = testScope.runTest {
        assertEquals( 1f, subject.userData.map { it.fontScale }.first() )
        listOf( 0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f, 2.25f, 2.5f, 2.75f, 3f )
            .forEach {
                subject.setFontScale( it )
                assertEquals( it, subject.userData.map { it.fontScale }.first() )
            }
    }

    @Test
    fun testChangeThemeMode() = testScope.runTest {
        assertEquals(
            ThemeMode.FOLLOW_SYSTEM,
            subject.userData.map { it.themeMode }.first()
        )
        ThemeMode.entries.forEach {
            subject.setThemeMode( it )
            assertEquals(
                it,
                subject.userData.map { it.themeMode }.first()
            )
        }
    }

    @Test
    fun testChangeUseMaterialYou() = testScope.runTest {
        assertTrue( subject.userData.map { it.useMaterialYou }.first() )
        subject.useMaterialYou( true )
        assertTrue( subject.userData.map { it.useMaterialYou }.first() )
        subject.useMaterialYou( false )
        assertFalse( subject.userData.map { it.useMaterialYou }.first() )
    }

    @Test
    fun testSetPrimaryColorName() = testScope.runTest {
        assertEquals(
            DefaultPreferences.PRIMARY_COLOR_NAME,
            subject.userData.map { it.primaryColorName }.first()
        )
        listOf( "Red", "Green", "Blue" ).forEach {
            subject.setPrimaryColorName( it )
            assertEquals(
                it,
                subject.userData.map { it.primaryColorName }.first()
            )
        }
    }

    @Test
    fun testSetBottomBarLabelVisibility() = testScope.runTest {
        assertEquals(
            DefaultPreferences.BOTTOM_BAR_LABEL_VISIBILITY,
            subject.userData.map { it.bottomBarLabelVisibility }.first()
        )
        BottomBarLabelVisibility.entries.forEach {
            subject.setBottomBarLabelVisibility( it )
            assertEquals(
                it,
                subject.userData.map { it.bottomBarLabelVisibility }.first()
            )
        }
    }

    @Test
    fun testSetFadePlayback() = testScope.runTest {
        assertFalse( subject.userData.map { it.fadePlayback }.first() )
        subject.setFadePlayback( true )
        assertTrue( subject.userData.map { it.fadePlayback }.first() )
        subject.setFadePlayback( false )
        assertFalse( subject.userData.map { it.fadePlayback }.first() )
    }

    @Test
    fun testSetFadePlaybackDuration() = testScope.runTest {
        assertEquals(
            DefaultPreferences.FADE_PLAYBACK_DURATION,
            subject.userData.map { it.fadePlaybackDuration }.first()
        )
        listOf( 0.5f, 1f, 1.5f, 2f, 6f ).forEach {
            subject.setFadePlaybackDuration( it )
            assertEquals(
                it,
                subject.userData.map { it.fadePlaybackDuration }.first()
            )
        }
    }

    @Test
    fun testSetRequireAudioFocus() = runTest {
        assertFalse( subject.userData.map { it.requireAudioFocus }.first() )
        subject.setRequireAudioFocus( true )
        assertTrue( subject.userData.map { it.requireAudioFocus }.first() )
        subject.setRequireAudioFocus( false )
        assertFalse( subject.userData.map { it.requireAudioFocus }.first() )
    }

    @Test
    fun testSetIgnoreAudioFocusLoss() = runTest {
        assertFalse( subject.userData.map { it.ignoreAudioFocusLoss }.first() )
        subject.setIgnoreAudioFocusLoss( true )
        assertTrue( subject.userData.map { it.ignoreAudioFocusLoss }.first() )
        subject.setIgnoreAudioFocusLoss( false )
        assertFalse( subject.userData.map { it.ignoreAudioFocusLoss }.first() )
    }

    @Test
    fun testSetPlayOnHeadphonesConnect() = runTest {
        assertFalse( subject.userData.map { it.playOnHeadphonesConnect }.first() )
        subject.setPlayOnHeadphonesConnect( true )
        assertTrue( subject.userData.map { it.playOnHeadphonesConnect }.first() )
        subject.setPlayOnHeadphonesConnect( false )
        assertFalse( subject.userData.map { it.playOnHeadphonesConnect }.first() )
    }

    @Test
    fun testSetPauseOnHeadphonesDisconnect() = runTest {
        assertFalse( subject.userData.map { it.pauseOnHeadphonesDisconnect }.first() )
        subject.setPauseOnHeadphonesDisconnect( true )
        assertTrue( subject.userData.map { it.pauseOnHeadphonesDisconnect }.first() )
        subject.setPauseOnHeadphonesDisconnect( false )
        assertFalse( subject.userData.map { it.pauseOnHeadphonesDisconnect }.first() )
    }

    @Test
    fun testSetFastRewindDuration() = runTest {
        assertEquals(
            DefaultPreferences.FAST_REWIND_DURATION,
            subject.userData.map { it.fastRewindDuration }.first()
        )
        listOf( 15, 30 ).forEach {
            subject.setFastRewindDuration( it )
            assertEquals(
                it,
                subject.userData.map { it.fastRewindDuration }.first()
            )
        }
    }

    @Test
    fun testSetFastForwardDuration() = runTest {
        assertEquals(
            DefaultPreferences.FAST_FORWARD_DURATION,
            subject.userData.map { it.fastForwardDuration }.first()
        )
        listOf( 15, 30 ).forEach {
            subject.setFastForwardDuration( it )
            assertEquals(
                it,
                subject.userData.map { it.fastForwardDuration }.first()
            )
        }
    }

    @Test
    fun testSetMiniPlayerTextMarquee() = runTest {
        assertFalse( subject.userData.map { it.miniPlayerTextMarquee }.first() )
        subject.setMiniPlayerTextMarquee( true )
        assertTrue( subject.userData.map { it.miniPlayerTextMarquee }.first() )
        subject.setMiniPlayerTextMarquee( false )
        assertFalse( subject.userData.map { it.miniPlayerTextMarquee }.first() )
    }

    @Test
    fun testSetMiniPlayerShowSeekControls() = runTest {
        assertFalse( subject.userData.map { it.miniPlayerShowSeekControls }.first() )
        subject.setMiniPlayerShowSeekControls( true )
        assertTrue( subject.userData.map { it.miniPlayerShowSeekControls }.first() )
        subject.setMiniPlayerShowSeekControls( false )
        assertFalse( subject.userData.map { it.miniPlayerShowSeekControls }.first() )
    }

    @Test
    fun testSetMiniPlayerShowTrackControls() = runTest {
        assertFalse( subject.userData.map { it.miniPlayerShowTrackControls }.first() )
        subject.setMiniPlayerShowTrackControls( true )
        assertTrue( subject.userData.map { it.miniPlayerShowTrackControls }.first() )
        subject.setMiniPlayerShowTrackControls( false )
        assertFalse( subject.userData.map { it.miniPlayerShowTrackControls }.first() )
    }

    @Test
    fun testSetLyricsLayout() = runTest {
        assertEquals(
            DefaultPreferences.LYRICS_LAYOUT,
            subject.userData.map { it.lyricsLayout }.first()
        )
        LyricsLayout.entries.forEach {
            subject.setLyricsLayout( it )
            assertEquals( it, subject.userData.map { it.lyricsLayout }.first() )
        }
    }

    @Test
    fun testSetShowNowPlayingAudioInformation() = runTest {
        assertFalse( subject.userData.map { it.showNowPlayingAudioInformation }.first() )
        subject.setShowNowPlayingAudioInformation( true )
        assertTrue( subject.userData.map { it.showNowPlayingAudioInformation }.first() )
        subject.setShowNowPlayingAudioInformation( false )
        assertFalse( subject.userData.map { it.showNowPlayingAudioInformation }.first() )
    }

    @Test
    fun testSetShowNowPlayingSeekControls() = runTest {
        assertFalse( subject.userData.map { it.showNowPlayingSeekControls }.first() )
        subject.setShowNowPlayingSeekControls( true )
        assertTrue( subject.userData.map { it.showNowPlayingSeekControls }.first() )
        subject.setShowNowPlayingSeekControls( false )
        assertFalse( subject.userData.map { it.showNowPlayingSeekControls }.first() )
    }

    @Test
    fun testSetPlaybackSpeed() = runTest {
        assertEquals(
            DefaultPreferences.PLAYBACK_SPEED,
            subject.userData.map { it.playbackSpeed }.first()
        )
        listOf( 0.5f, 1f, 1.5f, 2f ).forEach {
            subject.setPlaybackSpeed( it )
            assertEquals(
                it,
                subject.userData.map { it.playbackSpeed }.first()
            )
        }
    }

    @Test
    fun testSetPlaybackPitch() = runTest {
        assertEquals(
            DefaultPreferences.PLAYBACK_PITCH,
            subject.userData.map { it.playbackPitch }.first()
        )
        listOf( 0.5f, 1f, 1.5f, 2f ).forEach {
            subject.setPlaybackPitch( it )
            assertEquals(
                it,
                subject.userData.map { it.playbackPitch }.first()
            )
        }
    }

    @Test
    fun testSetLoopMode() = runTest {
        assertEquals(
            DefaultPreferences.LOOP_MODE,
            subject.userData.map { it.loopMode }.first()
        )
        LoopMode.entries.forEach {
            subject.setLoopMode( it )
            assertEquals(
                it,
                subject.userData.map { it.loopMode }.first()
            )
        }
    }

    @Test
    fun testSetShuffle() = runTest {
        assertFalse( subject.userData.map { it.shuffle }.first() )
        subject.setShuffle( true )
        assertTrue( subject.userData.map { it.shuffle }.first() )
        subject.setShuffle( false )
        assertFalse( subject.userData.map { it.shuffle }.first() )
    }

    @Test
    fun testSetShowLyrics() = runTest {
        assertFalse( subject.userData.map { it.showLyrics }.first() )
        subject.setShowLyrics( true )
        assertTrue( subject.userData.map { it.showLyrics }.first() )
        subject.setShowLyrics( false )
        assertFalse( subject.userData.map { it.showLyrics }.first() )
    }

    @Test
    fun testSetControlsLayoutIsDefault() = runTest {
        assertFalse( subject.userData.map { it.controlsLayoutDefault }.first() )
        subject.setControlsLayoutIsDefault( true )
        assertTrue( subject.userData.map { it.controlsLayoutDefault }.first() )
        subject.setControlsLayoutIsDefault( false )
        assertFalse( subject.userData.map { it.controlsLayoutDefault }.first() )
    }

    @Test
    fun testSetDisabledTreePaths() = runTest {
        assertTrue( subject.userData.map { it.currentlyDisabledTreePaths }.first().isEmpty() )
        subject.setDisabledTreePaths(
            setOf( "path-1", "path-2", "path-3", "path-4", "path-5" )
        )
        assertEquals(
            5,
            subject.userData.map { it.currentlyDisabledTreePaths }.first().size
        )
    }

    @Test
    fun testSetSortSongsBy() = runTest {
        assertEquals(
            DefaultPreferences.SORT_SONGS_BY,
            subject.userData.map { it.sortSongsBy }.first()
        )
        SortSongsBy.entries.forEach {
            subject.setSortSongsBy( it )
            assertEquals(
                it,
                subject.userData.map { it.sortSongsBy }.first()
            )
        }
    }

    @Test
    fun testSetSortSongsInReverse() = runTest {
        assertFalse( subject.userData.map { it.sortSongsReverse }.first() )
        subject.setSortSongsInReverse( true )
        assertTrue( subject.userData.map { it.sortSongsReverse }.first() )
        subject.setSortSongsInReverse( false )
        assertFalse( subject.userData.map { it.sortSongsReverse }.first() )
    }

    @Test
    fun testSetSortArtistsBy() = runTest {
        assertEquals(
            DefaultPreferences.SORT_ARTISTS_BY,
            subject.userData.map { it.sortArtistsBy }.first()
        )
        SortArtistsBy.entries.forEach { entry ->
            subject.setSortArtistsBy( entry )
            assertEquals(
                entry,
                subject.userData.map { it.sortArtistsBy }.first()
            )
        }
    }

    @Test
    fun testSetSortArtistsInReverse() = runTest {
        assertFalse( subject.userData.map { it.sortArtistsReverse }.first() )
        subject.setSortArtistsInReverse( true )
        assertTrue( subject.userData.map { it.sortArtistsReverse }.first() )
        subject.setSortArtistsInReverse( false )
        assertFalse( subject.userData.map { it.sortArtistsReverse }.first() )
    }

    @Test
    fun testSetSortGenresBy() = runTest {
        assertEquals(
            DefaultPreferences.SORT_GENRES_BY,
            subject.userData.map { it.sortGenresBy }.first()
        )
        SortGenresBy.entries.forEach { entry ->
            subject.setSortGenresBy( entry )
            assertEquals(
                entry,
                subject.userData.map { it.sortGenresBy }.first()
            )
        }
    }

    @Test
    fun testSetSortGenresInReverse() = runTest {
        assertFalse( subject.userData.map { it.sortGenresReverse }.first() )
        listOf( true, false ).forEach { reverse ->
            subject.setSortGenresInReverse( reverse )
            assertEquals(
                reverse,
                subject.userData.map { it.sortGenresReverse }.first()
            )
        }
    }

    @Test
    fun testSetSortPlaylistsBy() = runTest {
        assertEquals(
            DefaultPreferences.SORT_PLAYLISTS_BY,
            subject.userData.map { it.sortPlaylistsBy }.first()
        )
        SortPlaylistsBy.entries.forEach { entry ->
            subject.setSortPlaylistsBy( entry )
            assertEquals(
                entry,
                subject.userData.map { it.sortPlaylistsBy }.first()
            )
        }
    }

    @Test
    fun testSetSortPlaylistsInReverse() = runTest {
        assertFalse( subject.userData.map { it.sortPlaylistsReverse }.first() )
        listOf( true, false ).forEach { reverse ->
            subject.setSortPlaylistsInReverse( reverse )
            assertEquals(
                reverse,
                subject.userData.map { it.sortPlaylistsReverse }.first()
            )
        }
    }

    @Test
    fun testSetSortAlbumsBy() = runTest {
        assertEquals(
            DefaultPreferences.SORT_ALBUMS_BY,
            subject.userData.map { it.sortAlbumsBy }.first()
        )
        SortAlbumsBy.entries.forEach { entry ->
            subject.setSortAlbumsBy( entry )
            assertEquals(
                entry,
                subject.userData.map { it.sortAlbumsBy }.first()
            )
        }
    }

    @Test
    fun testSortAlbumsReverse() = runTest {
        assertFalse( subject.userData.map { it.sortAlbumsReverse }.first() )
        listOf( true, false ).forEach { reverse ->
            subject.setSortAlbumsInReverse( reverse )
            assertEquals(
                reverse,
                subject.userData.map { it.sortAlbumsReverse }.first()
            )
        }
    }

    @Test
    fun testSetSortPathsBy() = runTest {
        assertEquals(
            DefaultPreferences.SORT_PATHS_BY,
            subject.userData.map { it.sortPathsBy }.first()
        )
        SortPathsBy.entries.forEach { entry ->
            subject.setSortPathsBy( entry )
            assertEquals(
                entry,
                subject.userData.map { it.sortPathsBy }.first()
            )
        }
    }

    @Test
    fun testSetSortPathsInReverse() = runTest {
        assertFalse( subject.userData.map { it.sortPathsReverse }.first() )
        setOf( true, false ).forEach { reverse ->
            subject.setSortPathsInReverse( reverse )
            assertEquals(
                reverse,
                subject.userData.map { it.sortPathsReverse }.first()
            )
        }
    }

    @Test
    fun testSetCurrentlyPlayingSongId() = runTest {
        assertTrue( subject.userData.map { it.currentlyPlayingSongId }.first().isEmpty() )
        subject.setCurrentlyPlayingSongId( "song-id" )
        assertEquals(
            "song-id",
            subject.userData.map { it.currentlyPlayingSongId }.first()
        )
    }
}