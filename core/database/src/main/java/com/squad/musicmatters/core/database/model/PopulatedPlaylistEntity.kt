package com.squad.musicmatters.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class PopulatedPlaylistEntity(
    @Embedded val playlistEntity: PlaylistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "playlist_id"
    )
    val entries: List<PlaylistEntryEntity>
)