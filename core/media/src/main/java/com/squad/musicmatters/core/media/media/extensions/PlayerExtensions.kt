package com.squad.musicmatters.core.media.media.extensions

import androidx.media3.common.MediaItem
import androidx.media3.common.Player

fun Player.contains( mediaItem: MediaItem ): Boolean =
    getMediaItems().firstOrNull { it.mediaId == mediaItem.mediaId } != null

fun Player.getMediaItems(): List<MediaItem> {
    val mediaItems = mutableListOf<MediaItem>()
    if ( mediaItemCount > 0 ) {
        ( 0 until mediaItemCount ).forEach { pos ->
            mediaItems.add( getMediaItemAt( pos ) )
        }
    }
    return mediaItems
}

inline val Player.isPlayEnabled
    get() = ( availableCommands.contains( Player.COMMAND_PLAY_PAUSE ) &&
            ( !playWhenReady ) )

inline val Player.isEnded
    get() = playbackState == Player.STATE_ENDED

