package com.squad.musicmatters.core.data

import com.squad.musicmatters.core.model.SearchFilter
import com.squad.musicmatters.core.model.UserData
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    fun search(
        query: String,
        selectedSearchFilter: SearchFilter,
        userData: UserData,
    ): Flow<Map<SearchFilter, List<Any>>>

}
