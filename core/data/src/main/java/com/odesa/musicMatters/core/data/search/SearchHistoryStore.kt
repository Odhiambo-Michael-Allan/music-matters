package com.odesa.musicMatters.core.data.search

import com.odesa.musicMatters.core.model.SearchHistoryItem

interface SearchHistoryStore {
    fun fetchSearchHistory(): List<SearchHistoryItem>
    suspend fun saveSearchHistoryItem( searchHistoryItem: SearchHistoryItem )
    suspend fun deleteSearchHistoryItem( searchHistoryItem: SearchHistoryItem )
}