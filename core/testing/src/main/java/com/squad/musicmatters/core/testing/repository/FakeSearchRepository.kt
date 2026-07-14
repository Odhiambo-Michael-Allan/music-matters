package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.data.SearchRepository
import com.squad.musicmatters.core.model.SearchFilter
import com.squad.musicmatters.core.model.UserData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeSearchRepository : SearchRepository {

    private val searchResultsFlow: MutableSharedFlow<Map<SearchFilter, List<Any>>> =
        MutableSharedFlow( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST )

    override fun search(
        query: String,
        selectedSearchFilter: SearchFilter,
        userData: UserData
    ): Flow<Map<SearchFilter, List<Any>>> = searchResultsFlow

    fun sendResults( results: Map<SearchFilter, List<Any>> ) {
        searchResultsFlow.tryEmit( results )
    }

}