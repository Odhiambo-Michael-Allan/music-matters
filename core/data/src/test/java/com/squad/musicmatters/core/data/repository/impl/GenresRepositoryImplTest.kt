package com.squad.musicmatters.core.data.repository.impl

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.data.repository.GenresRepository
import com.squad.musicmatters.core.data.store.GenresStore
import com.squad.musicmatters.core.data.store.MediaStoreListener
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SortGenresBy
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GenresRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var genresStore: FakeGenresStore
    private lateinit var subject: GenresRepository

    @Before
    fun setUp() {
        genresStore = FakeGenresStore()
        subject = GenresRepositoryImpl(
            genresStore = genresStore,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun testFetchGenres() = runTest {
        val genres = listOf(
            Genre(
                id = 0,
                name = "A",
                numberOfTracks = 3,
            ),
            Genre(
                id = 1,
                name = "C",
                numberOfTracks = 4
            ),
            Genre(
                id = 2,
                name = "B",
                numberOfTracks = 2
            )
        )
        genresStore.sendGenres( genres )

        assertEquals(
            listOf(
                Genre(
                    id = 0,
                    name = "A",
                    numberOfTracks = 3,
                ),
                Genre(
                    id = 2,
                    name = "B",
                    numberOfTracks = 2
                ),
                Genre(
                    id = 1,
                    name = "C",
                    numberOfTracks = 4
                ),
            ),
            subject.fetchGenres( sortGenresBy = SortGenresBy.NAME ).first()
        )
    }

}

private class FakeGenresStore : GenresStore {

    private var genres = emptyList<Genre>()
    private val listeners = mutableListOf<MediaStoreListener>()

    override suspend fun fetchGenres(): List<Genre> = genres

    override suspend fun fetchGenreWith( id: Long ): Genre? =
        genres.find { it.id == id }

    override suspend fun searchGenresMatching( query: String ): List<Genre> =
        genres.filter { it.name.contains( query ) }

    override fun registerListener( listener: MediaStoreListener ) {
        listeners.add( listener )
    }

    override fun unregisterListener( listener: MediaStoreListener ) {
        listeners.remove( listener )
    }

    fun sendGenres( genres: List<Genre> ) {
        this.genres = genres
        listeners.forEach {
            it.onMediaStoreChanged()
        }
    }

}