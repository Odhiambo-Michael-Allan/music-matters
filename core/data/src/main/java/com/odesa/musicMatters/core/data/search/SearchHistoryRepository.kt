package com.odesa.musicMatters.core.data.search

import com.odesa.musicMatters.core.model.SearchHistoryItem
import kotlinx.coroutines.flow.StateFlow

interface SearchHistoryRepository {
    val searchHistory: StateFlow<List<SearchHistoryItem>>

    suspend fun saveSearchHistoryItem( searchHistoryItem: SearchHistoryItem )
    suspend fun deleteSearchHistoryItem( searchHistoryItem: SearchHistoryItem )

}