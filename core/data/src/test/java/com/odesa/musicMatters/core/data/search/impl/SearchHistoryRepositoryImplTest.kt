package com.odesa.musicMatters.core.data.search.impl

import com.odesa.musicMatters.core.data.search.SearchHistoryRepository
import com.odesa.musicMatters.core.data.search.SearchHistoryStore
import com.odesa.musicMatters.core.datatesting.search.testSearchHistoryItems
import com.odesa.musicMatters.core.datatesting.store.FakeSearchHistoryStore
import junit.framework.TestCase
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
        TestCase.assertEquals( 0, searchHistoryRepository.searchHistory.value.size )
        testSearchHistoryItems.forEach {
            searchHistoryRepository.saveSearchHistoryItem( it )
        }
        TestCase.assertEquals( testSearchHistoryItems.size, searchHistoryStore.fetchSearchHistory().size )
        TestCase.assertEquals( testSearchHistoryItems.size, searchHistoryRepository.searchHistory.value.size )
    }

    @Test
    fun testSearchHistoryItemsAreCorrectlyDeleted() = runTest {
        testSearchHistoryItems.forEach {
            searchHistoryRepository.saveSearchHistoryItem( it )
        }
        searchHistoryRepository.deleteSearchHistoryItem( testSearchHistoryItems.first() )
        searchHistoryRepository.deleteSearchHistoryItem( testSearchHistoryItems.last() )
        TestCase.assertEquals( testSearchHistoryItems.size - 2, searchHistoryStore.fetchSearchHistory().size )
        TestCase.assertEquals( testSearchHistoryItems.size - 2, searchHistoryRepository.searchHistory.value.size )
    }

    @Test
    fun testDuplicateEntriesAreCorrectlyHandled() = runTest {
        testSearchHistoryItems.forEach {
            searchHistoryRepository.saveSearchHistoryItem( it )
        }
        searchHistoryRepository.saveSearchHistoryItem( testSearchHistoryItems.last() )
        TestCase.assertEquals( testSearchHistoryItems.size, searchHistoryRepository.searchHistory.value.size )
        TestCase.assertEquals( testSearchHistoryItems.last().id, searchHistoryRepository.searchHistory.value.first().id )
    }
}