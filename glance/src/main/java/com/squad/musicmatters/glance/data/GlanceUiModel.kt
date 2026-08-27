package com.squad.musicmatters.glance.data

import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Song

data class GlanceUiModel(
    val isPlaying: Boolean,
    val shuffle: Boolean,
    val loopMode: LoopMode,
    val currentlyPlayingSong: GlanceSong?,
    val currentlyPlayingSongIsFavorite: Boolean,
    val songs: List<GlanceSong> = emptyList(),
)