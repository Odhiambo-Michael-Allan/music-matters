package com.squad.musicmatters.core.data.search

import com.squad.musicmatters.core.model.SearchHistoryItem

interface SearchHistoryStore {
    fun fetchSearchHistory(): List<SearchHistoryItem>
    suspend fun saveSearchHistoryItem( searchHistoryItem: SearchHistoryItem )
    suspend fun deleteSearchHistoryItem( searchHistoryItem: SearchHistoryItem )
}