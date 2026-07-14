package com.squad.musicmatters.feature.genres

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.testing.repository.FakeGenresRepository
import com.squad.musicmatters.core.testing.repository.FakeUserDataRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
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

    private lateinit var genresRepository: FakeGenresRepository
    private lateinit var preferencesDataSource: FakeUserDataRepository
    private lateinit var subject: GenresScreenViewModel

    @Before
    fun setUp() {
        genresRepository = FakeGenresRepository()
        preferencesDataSource = FakeUserDataRepository()
        subject = GenresScreenViewModel(
            genresRepository = genresRepository,
            userDataRepository = preferencesDataSource
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

        val genres = listOf(
            Genre(
                id = 0,
                name = "A",
                numberOfTracks = 2
            ),
            Genre(
                id = 1,
                name = "C",
                numberOfTracks = 5
            ),
            Genre(
                id = 2,
                name = "B",
                numberOfTracks = 2
            )
        )
        genresRepository.sendGenres( genres )
        preferencesDataSource.sendUserData( emptyUserData )
        assertEquals(
            GenresScreenUiState.Success(
                genres = listOf(
                    Genre(
                        id = 0,
                        name = "A",
                        numberOfTracks = 2
                    ),
                    Genre(
                        id = 2,
                        name = "B",
                        numberOfTracks = 2
                    ),
                    Genre(
                        id = 1,
                        name = "C",
                        numberOfTracks = 5
                    ),
                ),
                sortGenresBy = DefaultPreferences.SORT_GENRES_BY,
                sortGenresInReverse = false
            ),
            subject.uiState.value
        )
    }

}