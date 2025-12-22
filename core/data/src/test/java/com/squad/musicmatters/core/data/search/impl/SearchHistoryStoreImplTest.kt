package com.squad.musicmatters.core.data.search.impl

import com.squad.musicmatters.core.data.search.SearchHistoryStore
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

//class SearchHistoryStoreImplTest {
//
//    private val searchHistoryFile = File( "search-history-test-file.json" )
//    private lateinit var searchHistoryStore: SearchHistoryStore
//
//    @Before
//    fun setup() {
//        searchHistoryFile.createNewFile()
//        searchHistoryStore = SearchHistoryStoreImpl(
//            searchHistoryFile
//        )
//    }
//
//    @After
//    fun cleanup() {
//        searchHistoryFile.delete()
//    }
//
//    @Test
//    fun testSearchHistoryItemsAreCorrectlySaved() = runTest {
//        testSearchHistoryItems.forEach {
//            searchHistoryStore.saveSearchHistoryItem( it )
//        }
//        TestCase.assertEquals( testSearchHistoryItems.size, searchHistoryStore.fetchSearchHistory().size )
//    }
//
//    @Test
//    fun testSearchHistoryItemsAreSortedCorrectly() = runTest {
//        testSearchHistoryItems.forEach {
//            searchHistoryStore.saveSearchHistoryItem( it )
//        }
//        TestCase.assertEquals( testSearchHistoryItems.last().id,
//            searchHistoryStore.fetchSearchHistory().first().id )
//    }
//
//    @Test
//    fun testSearchHistoryItemsAreDeletedCorrectly() = runTest {
//        testSearchHistoryItems.forEach {
//            searchHistoryStore.saveSearchHistoryItem( it )
//        }
//        searchHistoryStore.deleteSearchHistoryItem( testSearchHistoryItems.first() )
//        searchHistoryStore.deleteSearchHistoryItem( testSearchHistoryItems.last() )
//        TestCase.assertEquals( testSearchHistoryItems.size - 2,
//            searchHistoryStore.fetchSearchHistory().size
//        )
//    }
//}