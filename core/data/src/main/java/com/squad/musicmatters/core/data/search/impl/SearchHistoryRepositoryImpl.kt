package com.squad.musicmatters.core.data.search.impl

import com.squad.musicmatters.core.data.search.SearchHistoryRepository
import com.squad.musicmatters.core.data.search.SearchHistoryStore
import com.squad.musicmatters.core.model.SearchHistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class SearchHistoryRepositoryImpl(
    private val searchHistoryStore: SearchHistoryStore
) : SearchHistoryRepository {

    private val _searchHistory = MutableStateFlow( searchHistoryStore.fetchSearchHistory() )
    override val searchHistory = _searchHistory.asStateFlow()

    override suspend fun saveSearchHistoryItem( searchHistoryItem: SearchHistoryItem) {
        withContext( Dispatchers.IO ) {
            if ( _searchHistory.value.contains( searchHistoryItem ) )
                deleteSearchHistoryItem( searchHistoryItem )
            searchHistoryStore.saveSearchHistoryItem( searchHistoryItem )
            _searchHistory.value = searchHistoryStore.fetchSearchHistory()
        }
    }

    override suspend fun deleteSearchHistoryItem( searchHistoryItem: SearchHistoryItem ) {
        withContext( Dispatchers.IO ) {
            searchHistoryStore.deleteSearchHistoryItem( searchHistoryItem )
            _searchHistory.value = searchHistoryStore.fetchSearchHistory()
        }
    }
}