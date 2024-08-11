package com.odesa.musicMatters.core.datatesting.store

import com.odesa.musicMatters.core.data.search.SearchHistoryStore
import com.odesa.musicMatters.core.model.SearchHistoryItem


class FakeSearchHistoryStore : SearchHistoryStore {

    private val searchHistory = mutableListOf<SearchHistoryItem>()

    override fun fetchSearchHistory() = searchHistory

    override suspend fun saveSearchHistoryItem( searchHistoryItem: SearchHistoryItem ) {
        searchHistory.add( 0, searchHistoryItem )
    }

    override suspend fun deleteSearchHistoryItem( searchHistoryItem: SearchHistoryItem ) {
        searchHistory.remove( searchHistoryItem )
    }
}