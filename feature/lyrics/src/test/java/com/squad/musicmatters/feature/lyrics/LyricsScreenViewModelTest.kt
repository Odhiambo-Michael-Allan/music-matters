package com.squad.musicmatters.feature.lyrics

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.testing.connection.FakeMusicMattersPlayer
import com.squad.musicmatters.core.testing.media.FakePlaybackPositionUpdater
import com.squad.musicmatters.core.testing.repository.FakeUserDataRepository
import com.squad.musicmatters.core.testing.repository.FakeQueueRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LyricsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var queueRepository: FakeQueueRepository
    private lateinit var preferencesDataSource: FakeUserDataRepository
    private lateinit var playbackPositionUpdater: FakePlaybackPositionUpdater
    private lateinit var player: FakeMusicMattersPlayer
    private lateinit var subject: LyricsScreenViewModel

    @Before
    fun setUp() {
        songsRepository = FakeSongsRepository()
        queueRepository = FakeQueueRepository()
        preferencesDataSource = FakeUserDataRepository()
        playbackPositionUpdater = FakePlaybackPositionUpdater()
        player = FakeMusicMattersPlayer()
        subject = LyricsScreenViewModel(
            player = player,
            songsRepository = songsRepository,
            queueRepository = queueRepository,
            userDataRepository = preferencesDataSource,
            playbackPositionUpdater = FakePlaybackPositionUpdater()
        )
    }

    @Test
    fun testStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }
        assertEquals( LyricsScreenUiState.Loading, subject.uiState.value )
    }

    @Test
    fun testUiStateIsSuccessWhenAllFlowsEmit() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        val testSongs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" )
        )

        val lyrics = listOf(
            Lyric(
                timeStamp = java.time.Duration.ZERO,
                content = "Young girls envy the life ya'll living"
            )
        )

        preferencesDataSource.sendUserData(
            emptyUserData.copy(
                currentlyPlayingSongId = "song-id-2"
            )
        )
        songsRepository.sendSongs( testSongs )
        songsRepository.sendLyrics( lyrics )
        queueRepository.sendSongs( testSongs )

        assertEquals(
            LyricsScreenUiState.Success(
                lyrics = lyrics
            ),
            subject.uiState.value
        )

    }

}