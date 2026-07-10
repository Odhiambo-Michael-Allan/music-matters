package com.squad.musicmatters.feature.genres

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.data.repository.GenreResult
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.testing.repository.FakeUserPreferencesRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsMetadataRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GenresScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var songsMetadataRepository: FakeSongsMetadataRepository
    private lateinit var preferencesDataSource: FakeUserPreferencesRepository
    private lateinit var subject: GenresScreenViewModel

    @Before
    fun setUp() {
        songsMetadataRepository = FakeSongsMetadataRepository()
        preferencesDataSource = FakeUserPreferencesRepository()
        subject = GenresScreenViewModel(
            songsMetadataRepository = songsMetadataRepository,
            userPreferencesRepository = preferencesDataSource
        )
    }

    @Test
    fun testStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        assertEquals(
            GenresScreenUiState.Loading,
            subject.uiState.value
        )
    }

    @Test
    fun testUiStateIsSuccessWhenAllRequiredFlowsEmit() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" )
        )
        val metadata = listOf(
            SongMetadata(
                songId = "song-id-1",
                codec = "",
                bitrate = 0,
                bitsPerSample = 0,
                genre = "Pop",
                samplingRate = 0f
            ),
            SongMetadata(
                songId = "song-id-2",
                codec = "",
                bitrate = 0,
                bitsPerSample = 0,
                genre = "Rap/HipHop",
                samplingRate = 0f
            ),
            SongMetadata(
                songId = "song-id-3",
                codec = "",
                bitrate = 0,
                bitsPerSample = 0,
                genre = "Rap/HipHop",
                samplingRate = 0f
            ),
        )
        songsMetadataRepository.sendMetadata( metadata )
        preferencesDataSource.sendUserData( emptyUserData )
        assertEquals(
            GenresScreenUiState.Success(
                genreResult = GenreResult.Success(
                    listOf(
                        Genre(
                            name = "Pop",
                            numberOfTracks = 1
                        ),
                        Genre(
                            name = "Rap/HipHop",
                            numberOfTracks = 2
                        )
                    )
                ),
                sortGenresBy = DefaultPreferences.SORT_GENRES_BY,
                sortGenresInReverse = false
            ),
            subject.uiState.value
        )
    }

}