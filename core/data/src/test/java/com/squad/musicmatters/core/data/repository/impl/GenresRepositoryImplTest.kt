package com.squad.musicmatters.core.data.repository.impl

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.data.repository.GenresRepository
import com.squad.musicmatters.core.data.store.GenresStore
import com.squad.musicmatters.core.data.store.MediaStoreListener
import com.squad.musicmatters.core.data.utils.sortGenres
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SortGenresBy
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    private var genres = MutableSharedFlow<List<Genre>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val listeners = mutableListOf<MediaStoreListener>()

    override fun fetchGenresFlow(
        sortGenresBy: SortGenresBy?,
        sortGenresInReverse: Boolean
    ): Flow<List<Genre>> = genres.map {
        it.sortGenres(
            by = sortGenresBy ?: DefaultPreferences.SORT_GENRES_BY,
            reverse = sortGenresInReverse
        )
    }


    override suspend fun fetchGenreWith( id: Long ): Genre? =
        genres.map { genres -> genres.find { it.id == id } }.first()

    override suspend fun searchGenresMatching(
        query: String,
        sortGenresBy: SortGenresBy?,
        sortGenresInReverse: Boolean
    ): List<Genre> = genres.map { genres ->
        genres.filter { it.name.contains( query ) }
    }.first()

    override suspend fun fetchSongIdsInGenre( genreId: Long ): Set<Long> {
        TODO("Not yet implemented")
    }

    override fun unregisterListener( listener: MediaStoreListener ) {
        listeners.remove( listener )
    }

    fun sendGenres( genres: List<Genre> ) {
        this.genres.tryEmit( genres )
        listeners.forEach {
            it.onMediaStoreChanged()
        }
    }

}