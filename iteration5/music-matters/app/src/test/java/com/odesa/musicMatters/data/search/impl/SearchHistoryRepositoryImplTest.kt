package com.odesa.musicMatters.data.search.impl

import com.odesa.musicMatters.data.search.SearchHistoryRepository
import com.odesa.musicMatters.data.search.SearchHistoryStore
import com.odesa.musicMatters.fakes.FakeSearchHistoryStore
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith( RobolectricTestRunner::class )
class SearchHistoryRepositoryImplTest {

    private lateinit var searchHistoryStore: SearchHistoryStore
    private lateinit var searchHistoryRepository: SearchHistoryRepository

    @Before
    fun setup() {
        searchHistoryStore = FakeSearchHistoryStore()
        searchHistoryRepository = SearchHistoryRepositoryImpl( searchHistoryStore )
    }

    @Test
    fun testSearchHistoryItemsAreCorrectlySaved() = runTest {
        assertEquals( 0, searchHistoryRepository.searchHistory.value.size )
        testSearchHistoryItems.forEach {
            searchHistoryRepository.saveSearchHistoryItem( it )
        }
        assertEquals( testSearchHistoryItems.size, searchHistoryStore.fetchSearchHistory().size )
        assertEquals( testSearchHistoryItems.size, searchHistoryRepository.searchHistory.value.size )
    }

    @Test
    fun testSearchHistoryItemsAreCorrectlyDeleted() = runTest {
        testSearchHistoryItems.forEach {
            searchHistoryRepository.saveSearchHistoryItem( it )
        }
        searchHistoryRepository.deleteSearchHistoryItem( testSearchHistoryItems.first() )
        searchHistoryRepository.deleteSearchHistoryItem( testSearchHistoryItems.last() )
        assertEquals( testSearchHistoryItems.size - 2, searchHistoryStore.fetchSearchHistory().size )
        assertEquals( testSearchHistoryItems.size - 2, searchHistoryRepository.searchHistory.value.size )
    }

    @Test
    fun testDuplicateEntriesAreCorrectlyHandled() = runTest {
        testSearchHistoryItems.forEach {
            searchHistoryRepository.saveSearchHistoryItem( it )
        }
        searchHistoryRepository.saveSearchHistoryItem( testSearchHistoryItems.last() )
        assertEquals( testSearchHistoryItems.size, searchHistoryRepository.searchHistory.value.size )
        assertEquals( testSearchHistoryItems.last().id, searchHistoryRepository.searchHistory.value.first().id )
    }
}