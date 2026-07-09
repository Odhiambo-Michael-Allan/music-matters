package com.squad.musicmatters.core.data.songs

import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongMetadata

interface MetadataStore {
    fun fetchMetadataFor( songs: List<Song> ): List<SongMetadata>
}