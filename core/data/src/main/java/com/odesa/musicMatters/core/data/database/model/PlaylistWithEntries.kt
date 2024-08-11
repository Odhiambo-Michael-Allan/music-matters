package com.odesa.musicMatters.core.data.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class PlaylistWithEntries(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "id",
        entityColumn = "playlist_id"
    )
    val entries: List<PlaylistEntry>
)