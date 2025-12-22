package com.squad.musicmatters.core.testing.media

import androidx.media3.common.MediaItem
import com.squad.musicmatters.core.media.media.library.AbstractMusicSource
import com.squad.musicmatters.core.media.media.library.STATE_ERROR
import com.squad.musicmatters.core.media.media.library.STATE_INITIALIZED

class FakeMusicSource (
    music: List<MediaItem>
) : AbstractMusicSource() {

    private var musicCatalog = emptyList<MediaItem>()

    init {
        musicCatalog = music
    }

    override suspend fun load() = Unit

    override fun delete( mediaItemId: String ) {
        musicCatalog = musicCatalog.filter { it.mediaId != mediaItemId }
    }

    override fun iterator() = musicCatalog.iterator()

    fun prepare() {
        state = STATE_INITIALIZED
    }

    fun error() {
        state = STATE_ERROR
    }
}