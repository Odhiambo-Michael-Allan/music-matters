package com.odesa.musicMatters.core.datatesting.repository

import com.odesa.musicMatters.core.data.search.SearchHistoryRepository
import com.odesa.musicMatters.core.model.SearchHistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSearchHistoryRepository : SearchHistoryRepository {

    private val _searchHistory = MutableStateFlow( mutableListOf<SearchHistoryItem>() )
    override val searchHistory = _searchHistory.asStateFlow()

    override suspend fun saveSearchHistoryItem( searchHistoryItem: SearchHistoryItem ) {
        _searchHistory.value.add( searchHistoryItem )
    }

    override suspend fun deleteSearchHistoryItem( searchHistoryItem: SearchHistoryItem ) {
        _searchHistory.value.remove( searchHistoryItem )
    }
}