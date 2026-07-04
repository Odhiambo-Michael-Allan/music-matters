package com.squad.musicmatters.feature.nowplaying

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.data.repository.impl.FAVORITES_PLAYLIST_ID
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.media.media.PlaybackPositionUpdater
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.testing.connection.FakeMusicMattersPlayer
import com.squad.musicmatters.core.testing.media.FakePlaybackPositionUpdater
import com.squad.musicmatters.core.testing.repository.FakePlaylistRepository
import com.squad.musicmatters.core.testing.repository.FakePreferencesDataSource
import com.squad.musicmatters.core.testing.repository.FakeQueueRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsAdditionalMetadataRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
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
    private lateinit var player: FakeMusicMattersPlayer
    private lateinit var playlistRepository: FakePlaylistRepository
    private lateinit var playbackPositionUpdater: FakePlaybackPositionUpdater
    private lateinit var metadataRepository: FakeSongsAdditionalMetadataRepository
    private lateinit var queueRepository: FakeQueueRepository
    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var preferencesDataSource: FakePreferencesDataSource

    @Before
    fun setUp() {
        player = FakeMusicMattersPlayer()
        playlistRepository = FakePlaylistRepository()
        playbackPositionUpdater = FakePlaybackPositionUpdater()
        metadataRepository = FakeSongsAdditionalMetadataRepository()
        songsRepository = FakeSongsRepository()
        queueRepository = FakeQueueRepository()
        preferencesDataSource = FakePreferencesDataSource()
        viewModel = NowPlayingScreenViewModel(
            player = player,
            preferencesDataSource = preferencesDataSource,
            playlistRepository = playlistRepository,
            playbackPositionUpdater = playbackPositionUpdater,
            songsAdditionalMetadataRepository = metadataRepository,
            queueRepository = queueRepository,
            songsRepository = songsRepository,
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
        songsRepository.sendSongs( emptyList() )

        assertEquals(
            NowPlayingScreenUiState.Success(
                playerState = PlayerState(),
                currentlyPlayingSong = null,
                userData = emptyUserData,
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
        songsRepository.sendSongs( testSongs )

        assertEquals(
            NowPlayingScreenUiState.Success(
                currentlyPlayingSong = testSongs[1],
                userData = emptyUserData,
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
        songsRepository.sendSongs( emptyList() )

        assertEquals(
            NowPlayingScreenUiState.Success(
                playerState = PlayerState(),
                currentlyPlayingSong = null,
                userData = emptyUserData,
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

