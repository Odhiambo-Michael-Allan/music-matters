package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import kotlinx.coroutines.flow.Flow

interface SongsRepository {
    fun fetchSongs(
        sortSongsBy: SortSongsBy? = null,
        sortSongsInReverse: Boolean? = false
    ): Flow<List<Song>>
}
