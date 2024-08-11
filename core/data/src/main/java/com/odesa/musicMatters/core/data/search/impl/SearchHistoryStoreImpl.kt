package com.odesa.musicMatters.core.data.search.impl


import com.odesa.musicMatters.core.data.FileAdapter
import com.odesa.musicMatters.core.data.search.SearchHistoryStore
import com.odesa.musicMatters.core.model.SearchHistoryItem
import com.odesa.musicMatters.core.model.toList
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class SearchHistoryStoreImpl( searchHistoryFile: File ): SearchHistoryStore {

    private var fileAdapter = FileAdapter( searchHistoryFile )

    override fun fetchSearchHistory() = getSavedSearchItems()

    override suspend fun saveSearchHistoryItem( searchHistoryItem: SearchHistoryItem) {
        val savedSearchHistoryItems = getSavedSearchItems().toMutableList()
        savedSearchHistoryItems.add( 0, searchHistoryItem )
        saveSearchItems( savedSearchHistoryItems )
    }

    override suspend fun deleteSearchHistoryItem( searchHistoryItem: SearchHistoryItem ) {
        val currentlySavedSearchHistoryItems = getSavedSearchItems().toMutableList()
        currentlySavedSearchHistoryItems.remove( searchHistoryItem )
        saveSearchItems( currentlySavedSearchHistoryItems )
    }

    private fun getSavedSearchItems(): List<SearchHistoryItem> {
        val fileContents = fileAdapter.read()
        if ( fileContents.isEmpty() ) return emptyList()
        val jsonObject = JSONObject( fileContents )
        if ( !jsonObject.has( SEARCH_HISTORY_ITEMS_LIST_KEY ) ) return emptyList()
        return jsonObject.getJSONArray( SEARCH_HISTORY_ITEMS_LIST_KEY ).toList {
            SearchHistoryItem.fromJSONObject( getJSONObject( it ) )
        }.mapNotNull { it }
    }

    private fun saveSearchItems( searchItems: List<SearchHistoryItem> ) {
        val jsonObject = JSONObject().apply {
            put( SEARCH_HISTORY_ITEMS_LIST_KEY, JSONArray( searchItems.map { it.toJSONObject() } ) )
        }
        fileAdapter.overwrite( jsonObject.toString() )
    }
}

private const val SEARCH_HISTORY_ITEMS_LIST_KEY = "SEARCH_HISTORY_ITEMS_LIST_KEY"