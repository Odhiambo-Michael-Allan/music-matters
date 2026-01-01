package com.squad.musicmatters.feature.nowplaying

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.data.repository.impl.FAVORITES_PLAYLIST_ID
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.testing.connection.TestMusicServiceConnection
import com.squad.musicmatters.core.testing.repository.TestPlaylistRepository
import com.squad.musicmatters.core.testing.repository.TestPreferencesDataSource
import com.squad.musicmatters.core.testing.repository.TestQueueRepository
import com.squad.musicmatters.core.testing.repository.TestSongsAdditionalMetadataRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class NowPlayingScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: NowPlayingScreenViewModel
    private lateinit var player: TestMusicServiceConnection
    private lateinit var playlistRepository: TestPlaylistRepository
    private lateinit var playbackPositionUpdater: TestPlaybackPositionUpdater
    private lateinit var metadataRepository: TestSongsAdditionalMetadataRepository
    private lateinit var queueRepository: TestQueueRepository
    private lateinit var preferencesDataSource: TestPreferencesDataSource

    @Before
    fun setUp() {
        player = TestMusicServiceConnection()
        playlistRepository = TestPlaylistRepository()
        playbackPositionUpdater = TestPlaybackPositionUpdater()
        metadataRepository = TestSongsAdditionalMetadataRepository()
        queueRepository = TestQueueRepository()
        preferencesDataSource = TestPreferencesDataSource()
        viewModel = NowPlayingScreenViewModel(
            player = player,
            preferencesDataSource = preferencesDataSource,
            playlistRepository = playlistRepository,
            playbackPositionUpdater = playbackPositionUpdater,
            songsAdditionalMetadataRepository = metadataRepository,
            queueRepository = queueRepository,
        )
    }

    @Test
    fun testUiStateInInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }

        assertEquals(
            NowPlayingScreenUiState.Loading,
            viewModel.uiState.value
        )
    }

    @Test
    fun testUiStateIsSuccessWhenAllFlowsEmit() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }

        preferencesDataSource.sendUserData( emptyUserData )
        playlistRepository.sendPlaylists( emptyList() )
        metadataRepository.sendMetadata( emptyList() )
        queueRepository.sendSongs( emptyList() )

        assertEquals(
            NowPlayingScreenUiState.Success(
                playerState = PlayerState(),
                userData = emptyUserData,
                queue = emptyList(),
                currentlyPlayingSongIsFavorite = false,
                playlists = emptyList(),
                songAdditionalMetadata = null
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun testUiStateIsUpdatedCorrectly() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }

        val testSongs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" )
        )

        val playerState = PlayerState(
            currentlyPlayingSongId = "song-id-2",
            isPlaying = true,
            isBuffering = false,
        )
        val metadataList = listOf(
            SongAdditionalMetadata(
                songId = "song-id-1",
                codec = "",
                bitsPerSample = 0L,
                bitrate = 0L,
                samplingRate = 0f,
                genre = ""
            )
        )
        player.sendPlayerState( playerState )
        preferencesDataSource.sendUserData( emptyUserData )
        playlistRepository.sendPlaylists( emptyList() )
        playlistRepository.addToFavorites( testSong( "song-id-2" ) )
        metadataRepository.sendMetadata( metadataList )
        queueRepository.sendSongs( testSongs )

        assertEquals(
            NowPlayingScreenUiState.Success(
                userData = emptyUserData,
                queue = testSongs,
                currentlyPlayingSongIsFavorite = true,
                playerState = playerState,
                playlists = listOf(
                    Playlist(
                        id = FAVORITES_PLAYLIST_ID,
                        title = "",
                        songIds = setOf( "song-id-2" )
                    )
                ),
                songAdditionalMetadata = null
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun testPlaybackPositionChange() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.playbackPosition.collect() }

        assertEquals(
            PlaybackPosition.ZERO,
            viewModel.playbackPosition.value
        )

        val playbackPosition = PlaybackPosition(
            played = 3L,
            total = 5L,
            buffered = 4L
        )
        playbackPositionUpdater.setPlaybackPosition( playbackPosition )

        assertEquals(
            playbackPosition,
            viewModel.playbackPosition.value
        )
    }

    @Test
    fun whenSleepTimerChanges_uiStateIsUpdated() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }

        preferencesDataSource.sendUserData( emptyUserData )
        playlistRepository.sendPlaylists( emptyList() )
        metadataRepository.sendMetadata( emptyList() )
        queueRepository.sendSongs( emptyList() )

        assertEquals(
            NowPlayingScreenUiState.Success(
                playerState = PlayerState(),
                userData = emptyUserData,
                queue = emptyList(),
                currentlyPlayingSongIsFavorite = false,
                playlists = emptyList(),
                songAdditionalMetadata = null,
                sleepTimer = null,
            ),
            viewModel.uiState.value
        )

        viewModel.startSleepTimer( 500000L.toDuration( DurationUnit.MILLISECONDS ) )

        assertNotNull( ( viewModel.uiState.value as? NowPlayingScreenUiState.Success )?.sleepTimer )
    }

}

private class TestPlaybackPositionUpdater : PlaybackPositionUpdater {
    private val _playbackPosition = MutableStateFlow( PlaybackPosition.ZERO )
    override val playbackPosition = _playbackPosition.asStateFlow()

    override fun startPeriodicUpdates() {
        TODO("Not yet implemented")
    }

    override fun stopPeriodicUpdates() {
        TODO("Not yet implemented")
    }

    override fun cleanUp() {
        TODO("Not yet implemented")
    }

    fun setPlaybackPosition( playbackPosition: PlaybackPosition ) {
        _playbackPosition.tryEmit( playbackPosition )
    }
}