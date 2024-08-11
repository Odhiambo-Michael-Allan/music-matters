package com.odesa.musicMatters.core.common.media

import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.DeviceInfo
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Player.EVENT_MEDIA_ITEM_TRANSITION
import androidx.media3.common.Player.EVENT_MEDIA_METADATA_CHANGED
import androidx.media3.common.Player.EVENT_POSITION_DISCONTINUITY
import androidx.media3.common.Player.EVENT_TIMELINE_CHANGED
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi
import kotlin.math.min

/**
 * A [Player] implementation that delegates to an actual [Player] implementation that is
 * replaceable by another instance by calling [setPlayer].
 */
class ReplaceableForwardingPlayer( private var player: Player ) : Player {

    private val listeners: MutableList<Player.Listener> = arrayListOf()
    private val playlist: MutableList<MediaItem> = arrayListOf()
    private var currentMediaItemIndex: Int = 0
    private val playerListener: Player.Listener = PlayerListener()

    init {
        player.addListener( playerListener )
    }

    /**
     * Sets a new [Player] instance to which the state of the previous player is transferred
     */
    fun setPlayer( player: Player ) {
        // Add/remove all listeners before changing the player state.
        for ( listener in listeners ) {
            this.player.removeListener( listener )
            player.addListener( listener )
        }
        // Add/remove our listener we use to workaround the missing metadata support of CastPlayer
        this.player.removeListener( playerListener )
        player.addListener( playerListener )

        player.repeatMode = this.player.repeatMode
        player.shuffleModeEnabled = this.player.shuffleModeEnabled
        player.playlistMetadata = this.player.playlistMetadata
        player.trackSelectionParameters = this.player.trackSelectionParameters
        player.volume = this.player.volume
        player.playWhenReady = this.player.playWhenReady

        // Prepare the new player.
        player.setMediaItems( playlist, currentMediaItemIndex, this.player.contentPosition )
        player.prepare()

        // Stop the previous player. Don't release so it can be used again.
        this.player.clearMediaItems()
        this.player.stop()

        this.player = player
    }

    override fun getApplicationLooper(): Looper {
        return player.applicationLooper
    }

    override fun addListener( listener: Player.Listener ) {
        player.addListener( listener )
        listeners.add( listener )
    }

    override fun removeListener( listener: Player.Listener ) {
        player.removeListener( listener )
        listeners.remove( listener )
    }

    override fun setMediaItems( mediaItems: MutableList<MediaItem> ) {
        player.setMediaItems( mediaItems )
        playlist.clear()
        playlist.addAll( mediaItems )
    }

    override fun setMediaItems( mediaItems: MutableList<MediaItem>, resetPosition: Boolean ) {
        player.setMediaItems( mediaItems, resetPosition )
        playlist.clear()
        playlist.addAll( mediaItems )
    }

    override fun setMediaItems(
        mediaItems: MutableList<MediaItem>,
        startWindowIndex: Int,
        startPositionMs: Long
    ) {
        player.setMediaItems( mediaItems, startWindowIndex, startPositionMs )
        playlist.clear()
        playlist.addAll( mediaItems )
    }

    override fun setMediaItem( mediaItem: MediaItem ) {
        player.setMediaItem( mediaItem )
        playlist.clear()
        playlist.add( mediaItem )
    }

    override fun setMediaItem( mediaItem: MediaItem, startPositionMs: Long ) {
        player.setMediaItem( mediaItem, startPositionMs )
        playlist.clear()
        playlist.add( mediaItem )
    }

    override fun setMediaItem( mediaItem: MediaItem, resetPosition: Boolean ) {
        player.setMediaItem( mediaItem, resetPosition )
        playlist.clear()
        playlist.add( mediaItem )
    }

    override fun addMediaItem( mediaItem: MediaItem ) {
        player.addMediaItem( mediaItem )
        playlist.add( mediaItem )
    }

    override fun addMediaItem( index: Int, mediaItem: MediaItem ) {
        player.addMediaItem( index, mediaItem )
        playlist.add( index, mediaItem )
    }

    override fun addMediaItems( mediaItems: MutableList<MediaItem> ) {
        player.addMediaItems( mediaItems )
        playlist.addAll( mediaItems )
    }

    override fun addMediaItems( index: Int, mediaItems: MutableList<MediaItem> ) {
        player.addMediaItems( index, mediaItems )
        playlist.addAll( index, mediaItems )
    }

    override fun moveMediaItem( currentIndex: Int, newIndex: Int ) {
        player.moveMediaItem( currentIndex, newIndex )
        playlist.add( min( newIndex, playlist.size ), playlist.removeAt( currentIndex ) )
    }

    override fun moveMediaItems( fromIndex: Int, toIndex: Int, newIndex: Int ) {
        val removedItems: ArrayDeque<MediaItem> = ArrayDeque()
        val removedItemsLength = toIndex - fromIndex
        for ( i in removedItemsLength - 1 downTo 0 ) {
            removedItems.addFirst( playlist.removeAt( fromIndex + i ) )
        }
        playlist.addAll( min( newIndex, playlist.size ), removedItems )
    }

    override fun replaceMediaItem( index: Int, mediaItem: MediaItem ) {
        player.replaceMediaItem( index, mediaItem )
    }

    override fun replaceMediaItems(
        fromIndex: Int,
        toIndex: Int,
        mediaItems: MutableList<MediaItem>
    ) {
        player.replaceMediaItems( fromIndex, toIndex, mediaItems )
    }

    override fun removeMediaItem( index: Int ) {
        player.removeMediaItem( index )
        playlist.removeAt( index )
    }

    override fun removeMediaItems( fromIndex: Int, toIndex: Int ) {
        player.removeMediaItems( fromIndex, toIndex )
        val removedItemsLength = toIndex - fromIndex
        for ( i in removedItemsLength - 1 downTo 0 ) {
            playlist.removeAt( fromIndex + i )
        }
    }

    override fun clearMediaItems() {
        player.clearMediaItems()
        playlist.clear()
    }

    override fun isCommandAvailable( command: Int ) = player.isCommandAvailable( command )
    override fun canAdvertiseSession() = player.canAdvertiseSession()
    override fun getAvailableCommands() = player.availableCommands

    override fun prepare() {
        player.prepare()
    }

    override fun getPlaybackState() = player.playbackState
    override fun getPlaybackSuppressionReason() = player.playbackSuppressionReason
    override fun isPlaying() = player.isPlaying
    override fun getPlayerError() = player.playerError

    override fun play() {
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun setPlayWhenReady( playWhenReady: Boolean ) {
        player.playWhenReady = playWhenReady
    }

    override fun getPlayWhenReady() = player.playWhenReady

    override fun setRepeatMode( repeatMode: Int ) {
        player.repeatMode = repeatMode
    }

    override fun getRepeatMode() = player.repeatMode

    override fun setShuffleModeEnabled( shuffleModeEnabled: Boolean ) {
        player.shuffleModeEnabled = shuffleModeEnabled
    }

    override fun getShuffleModeEnabled() = player.shuffleModeEnabled
    override fun isLoading() = player.isLoading

    override fun seekToDefaultPosition() {
        player.seekToDefaultPosition()
    }

    override fun seekToDefaultPosition( windowIndex: Int ) {
        player.seekToDefaultPosition( windowIndex )
    }

    override fun seekTo(positionMs: Long) {
        player.seekTo( positionMs )
    }

    override fun seekTo( windowIndex: Int, positionMs: Long ) {
        player.seekTo( windowIndex, positionMs )
    }

    override fun getSeekBackIncrement(): Long {
        return player.seekBackIncrement
    }

    override fun seekBack() {
        player.seekBack()
    }

    override fun getSeekForwardIncrement(): Long {
        return player.seekForwardIncrement
    }

    override fun seekForward() {
        player.seekForward()
    }

    @Deprecated( "Deprecated in Java" )
    @UnstableApi
    override fun hasPrevious(): Boolean {
        return player.hasPrevious()
    }

    @Deprecated( "Deprecated in Java" )
    @UnstableApi
    override fun hasPreviousWindow(): Boolean {
        return player.hasPreviousWindow()
    }

    override fun hasPreviousMediaItem(): Boolean {
        return player.hasPreviousMediaItem()
    }

    @Deprecated( "Deprecated in Java" )
    @UnstableApi
    override fun previous() {
        player.previous()
    }

    @Deprecated( "Deprecated in Java" )
    @UnstableApi
    override fun seekToPreviousWindow() {
        player.seekToPreviousWindow()
    }

    override fun seekToPreviousMediaItem() {
        player.seekToPreviousMediaItem()
    }

    override fun getMaxSeekToPreviousPosition(): Long {
        return player.maxSeekToPreviousPosition
    }

    override fun seekToPrevious() {
        player.seekToPrevious()
    }

    @Deprecated( "Deprecated in Java" )
    @UnstableApi
    override fun hasNext(): Boolean {
        return player.hasNext()
    }

    @Deprecated( "Deprecated in Java" )
    @UnstableApi
    override fun hasNextWindow(): Boolean {
        return player.hasNextWindow()
    }

    override fun hasNextMediaItem(): Boolean {
        return player.hasNextMediaItem()
    }

    @Deprecated( "Deprecated in Java" )
    @UnstableApi
    override fun next() {
        player.next()
    }

    @Deprecated( "Deprecated in Java" )
    @UnstableApi
    override fun seekToNextWindow() {
        player.seekToNextWindow()
    }

    override fun seekToNextMediaItem() {
        player.seekToNextMediaItem()
    }

    override fun seekToNext() {
        player.seekToNext()
    }

    override fun setPlaybackParameters( playbackParameters: PlaybackParameters ) {
        player.playbackParameters = playbackParameters
    }

    override fun setPlaybackSpeed(speed: Float) {
        player.setPlaybackSpeed(speed)
    }

    override fun getPlaybackParameters(): PlaybackParameters {
        return player.playbackParameters
    }

    override fun stop() {
        player.stop()
    }

    override fun release() {
        player.release()
        playlist.clear()
    }

    override fun getCurrentTracks(): Tracks {
        return player.currentTracks
    }

    override fun getTrackSelectionParameters(): TrackSelectionParameters {
        return player.trackSelectionParameters
    }

    override fun setTrackSelectionParameters( parameters: TrackSelectionParameters) {
        player.trackSelectionParameters = parameters
    }

    override fun getMediaMetadata(): MediaMetadata {
        return player.mediaMetadata
    }

    override fun getPlaylistMetadata(): MediaMetadata {
        return player.playlistMetadata
    }

    override fun setPlaylistMetadata( mediaMetadata: MediaMetadata ) {
        player.playlistMetadata = mediaMetadata
    }

    @UnstableApi
    override fun getCurrentManifest(): Any? {
        return player.currentManifest
    }

    override fun getCurrentTimeline(): Timeline {
        return player.currentTimeline
    }

    override fun getCurrentPeriodIndex(): Int {
        return player.currentPeriodIndex
    }

    @Deprecated( "Deprecated in Java" )
    @UnstableApi
    override fun getCurrentWindowIndex(): Int {
        return player.currentWindowIndex
    }

    override fun getCurrentMediaItemIndex(): Int {
        return player.currentMediaItemIndex
    }

    @Deprecated( "Deprecated in Java" )
    @UnstableApi
    override fun getNextWindowIndex(): Int {
        return player.nextWindowIndex
    }

    override fun getNextMediaItemIndex(): Int {
        return player.nextMediaItemIndex
    }

    @Deprecated( "Deprecated in Java" )
    @UnstableApi
    override fun getPreviousWindowIndex(): Int {
        return player.previousWindowIndex
    }

    override fun getPreviousMediaItemIndex(): Int {
        return player.previousMediaItemIndex
    }

    override fun getCurrentMediaItem(): MediaItem? {
        return player.currentMediaItem
    }

    override fun getMediaItemCount(): Int {
        return player.mediaItemCount
    }

    override fun getMediaItemAt( index: Int ): MediaItem {
        return player.getMediaItemAt( index )
    }

    override fun getDuration(): Long {
        return player.duration
    }

    override fun getCurrentPosition(): Long {
        return player.currentPosition
    }

    override fun getBufferedPosition(): Long {
        return player.bufferedPosition
    }

    override fun getBufferedPercentage(): Int {
        return player.bufferedPercentage
    }

    override fun getTotalBufferedDuration(): Long {
        return player.totalBufferedDuration
    }

    @Deprecated( "Deprecated in Java" )
    @UnstableApi
    override fun isCurrentWindowDynamic(): Boolean {
        return player.isCurrentWindowDynamic
    }

    override fun isCurrentMediaItemDynamic(): Boolean {
        return player.isCurrentMediaItemDynamic
    }

    @Deprecated( "Deprecated in Java" )
    @UnstableApi
    override fun isCurrentWindowLive(): Boolean {
        return player.isCurrentWindowLive
    }

    override fun isCurrentMediaItemLive(): Boolean {
        return player.isCurrentMediaItemLive
    }

    override fun getCurrentLiveOffset(): Long {
        return player.currentLiveOffset
    }

    @Deprecated( "Deprecated in Java" )
    @UnstableApi
    override fun isCurrentWindowSeekable(): Boolean {
        return player.isCurrentWindowSeekable
    }

    override fun isCurrentMediaItemSeekable(): Boolean {
        return player.isCurrentMediaItemSeekable
    }

    override fun isPlayingAd(): Boolean {
        return player.isPlayingAd
    }

    override fun getCurrentAdGroupIndex(): Int {
        return player.currentAdGroupIndex
    }

    override fun getCurrentAdIndexInAdGroup(): Int {
        return player.currentAdIndexInAdGroup
    }

    override fun getContentDuration(): Long {
        return player.contentDuration
    }

    override fun getContentPosition(): Long {
        return player.contentPosition
    }

    override fun getContentBufferedPosition(): Long {
        return player.contentBufferedPosition
    }

    override fun getAudioAttributes(): AudioAttributes {
        return player.audioAttributes
    }

    override fun setVolume( volume: Float ) {
        player.volume = volume
    }

    override fun getVolume(): Float {
        return player.volume
    }

    override fun clearVideoSurface() {
        player.clearVideoSurface()
    }

    override fun clearVideoSurface( surface: Surface? ) {
        player.clearVideoSurface( surface )
    }

    override fun setVideoSurface( surface: Surface? ) {
        player.setVideoSurface( surface )
    }

    override fun setVideoSurfaceHolder( surfaceHolder: SurfaceHolder? ) {
        player.setVideoSurfaceHolder( surfaceHolder )
    }

    override fun clearVideoSurfaceHolder( surfaceHolder: SurfaceHolder? ) {
        player.clearVideoSurfaceHolder(surfaceHolder)
    }

    override fun setVideoSurfaceView(surfaceView: SurfaceView?) {
        return player.setVideoSurfaceView(surfaceView)
    }

    override fun clearVideoSurfaceView( surfaceView: SurfaceView? ) {
        return player.clearVideoSurfaceView( surfaceView )
    }

    override fun setVideoTextureView( textureView: TextureView? ) {
        return player.setVideoTextureView( textureView )
    }

    override fun clearVideoTextureView( textureView: TextureView? ) {
        return player.clearVideoTextureView( textureView )
    }

    override fun getVideoSize(): VideoSize {
        return player.videoSize
    }

    @UnstableApi
    override fun getSurfaceSize(): Size {
        return player.surfaceSize
    }

    override fun getCurrentCues(): CueGroup {
        return player.currentCues
    }

    override fun getDeviceInfo(): DeviceInfo {
        return player.deviceInfo
    }

    override fun getDeviceVolume(): Int {
        return player.deviceVolume
    }

    override fun isDeviceMuted(): Boolean {
        return player.isDeviceMuted
    }

    @Deprecated( "Deprecated in Java" )
    override fun setDeviceVolume( volume: Int ) {
        player.deviceVolume = volume
    }

    override fun setDeviceVolume( volume: Int, flags: Int ) {
        player.setDeviceVolume( volume, flags )
    }

    @Deprecated( "Deprecated in Java" )
    override fun increaseDeviceVolume() {
        player.increaseDeviceVolume()
    }

    override fun increaseDeviceVolume( flags: Int ) {
        player.increaseDeviceVolume( flags )
    }

    @Deprecated( "Deprecated in Java" )
    override fun decreaseDeviceVolume() {
        player.decreaseDeviceVolume()
    }

    override fun decreaseDeviceVolume( flags: Int ) {
        player.decreaseDeviceVolume( flags )
    }

    @Deprecated( "Deprecated in Java" )
    override fun setDeviceMuted( muted: Boolean ) {
        player.isDeviceMuted = muted
    }

    override fun setDeviceMuted( muted: Boolean, flags: Int ) {
        player.setDeviceMuted( muted, flags )
    }

    override fun setAudioAttributes( audioAttributes: AudioAttributes, handleAudioFocus: Boolean ) {
        player.setAudioAttributes( audioAttributes, handleAudioFocus )
    }

    private inner class PlayerListener : Player.Listener {
        override fun onEvents( player: Player, events: Player.Events ) {
            if ( events.contains( EVENT_MEDIA_ITEM_TRANSITION )
                && !events.contains( EVENT_MEDIA_METADATA_CHANGED ) ) {
                // CastPlayer does not support onMetaDataChange. We can trigger this here when the
                // media item changes.
                if ( playlist.isNotEmpty() ) {
                    for ( listener in listeners ) {
                        listener.onMediaMetadataChanged(
                            playlist[ player.currentMediaItemIndex ].mediaMetadata
                        )
                    }
                }
            }
            if ( events.contains( EVENT_POSITION_DISCONTINUITY )
                || events.contains( EVENT_MEDIA_ITEM_TRANSITION )
                || events.contains( EVENT_TIMELINE_CHANGED ) ) {
                if ( !player.currentTimeline.isEmpty ) {
                    currentMediaItemIndex = player.currentMediaItemIndex
                }
            }
        }
    }
}