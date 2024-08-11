package com.odesa.musicMatters.core.datatesting.media

import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.DeviceInfo
import androidx.media3.common.FlagSet
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Player.EVENT_MEDIA_ITEM_TRANSITION
import androidx.media3.common.Player.EVENT_MEDIA_METADATA_CHANGED
import androidx.media3.common.Player.EVENT_PLAYBACK_STATE_CHANGED
import androidx.media3.common.Player.EVENT_PLAY_WHEN_READY_CHANGED
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.STATE_IDLE
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi

@UnstableApi
class FakePlayer : Player {

    private val listeners: MutableList<Player.Listener> = mutableListOf()
    private val queue: MutableList<MediaItem> = mutableListOf()
    private var currentMediaItemIndex = 0
    private var currentMediaItem: MediaItem? = null
    private var playbackParameters = PlaybackParameters( 1f, 1f )
    private var repeatMode: Int = REPEAT_MODE_OFF
    private var playbackState = Player.STATE_IDLE

    override fun getCurrentMediaItemIndex() = currentMediaItemIndex

    override fun getCurrentMediaItem(): MediaItem? = currentMediaItem

    override fun setPlaybackParameters( playbackParameters: PlaybackParameters ) {
        this.playbackParameters = playbackParameters
    }

    override fun getPlaybackParameters() = playbackParameters

    override fun setPlaybackSpeed( speed: Float ) {
        this.playbackParameters = PlaybackParameters(
            speed,
            this.playbackParameters.pitch
        )
    }

    override fun setRepeatMode( repeatMode: Int ) {
        this.repeatMode = repeatMode
    }

    override fun getRepeatMode() = this.repeatMode

    override fun moveMediaItem( currentIndex: Int, newIndex: Int ) {
        val itemToMove = queue.removeAt( currentIndex )
        queue.add( newIndex, itemToMove )
        if ( currentIndex == currentMediaItemIndex ) currentMediaItemIndex = newIndex
        listeners.forEach {
            it.onEvents(
                this,
                Player.Events(
                    FlagSet.Builder().add( EVENT_MEDIA_METADATA_CHANGED ).build()
                )
            )
        }
    }

    /**
     * Replaces the media items at the given range of the playlist.
     * Implementations of this method may attempt to seamlessly continue
     * playback if the currently playing media item is replaced with a
     * compatible one (e.g. same URL, only metadata has changed).
     * This method must only be called if COMMAND_CHANGE_MEDIA_ITEMS is available.
     * Note that it is possible to replace a range with an arbitrary number
     * of new items, so that the number of removed items defined by
     * fromIndex and toIndex does not have to match the number of
     * added items defined by mediaItems. As result, it may also
     * change the index of subsequent items not touched by this operation.
     * Specified by:
     * replaceMediaItems in interface Player
     * Params:
     * fromIndex – The start of the range. If the index is larger
     * than the size of the playlist, the request is ignored.
     * toIndex – The first item not to be included in the range (exclusive).
     * If the index is larger than the size of the playlist, items up to the
     * end of the playlist are replaced
     */
    override fun replaceMediaItems(
        fromIndex: Int,
        toIndex: Int,
        mediaItems: MutableList<MediaItem>
    ) {
        if ( fromIndex >= queue.size ) return
        var positionInNewList = 0
        for ( pos in fromIndex until toIndex ) {
            replaceMediaItem( pos, mediaItems[ positionInNewList++ ])
        }
    }

    override fun replaceMediaItem( index: Int, mediaItem: MediaItem ) {
        queue.removeAt( index )
        queue.add( index, mediaItem )
        listeners.forEach {
            it.onEvents(
                this,
                Player.Events(
                    FlagSet.Builder().add( EVENT_MEDIA_METADATA_CHANGED ).build()
                )
            )
        }
    }

    override fun clearMediaItems() {
        queue.clear()
    }

    override fun setMediaItems(
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ) {
        setMediaItems( mediaItems )
    }

    override fun setMediaItems( mediaItems: MutableList<MediaItem>, resetPosition: Boolean ) {
        setMediaItems( mediaItems )
    }

    /**
     * Clears the playlist, adds the specified media items and resets the position to the
     * default position
     */
    override fun setMediaItems( mediaItems: MutableList<MediaItem> ) {
        queue.clear()
        queue.addAll( mediaItems )
    }

    override fun play() {
        this.playbackState = STATE_READY
        currentMediaItemIndex = 0
        currentMediaItem = queue[ currentMediaItemIndex ]
        listeners.forEach {
            it.onEvents(
                this,
                Player.Events(
                    FlagSet.Builder().add( EVENT_PLAY_WHEN_READY_CHANGED ).build()
                )
            )
        }
    }

    override fun getPlaybackState() = this.playbackState

    override fun getPlayWhenReady() = getPlaybackState() == STATE_READY

    override fun getDuration() = 0L

    override fun addMediaItem( index: Int, mediaItem: MediaItem ) {
        queue.add( index, mediaItem )
    }

    override fun getMediaItemCount() = queue.size

    override fun addListener( listener: Player.Listener ) {
        listeners.add( listener )
    }

    override fun removeListener( listener: Player.Listener ) {
        listeners.remove( listener )
    }

    override fun getMediaItemAt( index: Int ) = queue[ index ]

    override fun seekToNext() {
        currentMediaItemIndex = ++currentMediaItemIndex
        currentMediaItem = queue[ currentMediaItemIndex ]
        listeners.forEach {
            it.onEvents(
                this,
                Player.Events(
                    FlagSet.Builder().add(
                        EVENT_MEDIA_ITEM_TRANSITION
                    ).build()
                )
            )
        }
    }

    override fun isPlaying() = this.playbackState == STATE_READY

    override fun pause() {
        this.playbackState = STATE_IDLE
        listeners.forEach {
            it.onEvents(
                this,
                Player.Events(
                    FlagSet.Builder().add(
                        EVENT_PLAYBACK_STATE_CHANGED
                    ).build()
                )
            )
        }
    }

    override fun seekToPrevious() {
        currentMediaItemIndex = if ( currentMediaItemIndex > 0 ) --currentMediaItemIndex else currentMediaItemIndex
        currentMediaItem = queue[ currentMediaItemIndex ]
        listeners.forEach {
            it.onEvents(
                this,
                Player.Events(
                    FlagSet.Builder().add(
                        EVENT_MEDIA_ITEM_TRANSITION
                    ).build()
                )
            )
        }
    }

    // -------------------------------------------------------------------------

    override fun getApplicationLooper(): Looper {
        TODO("Not yet implemented")
    }

    override fun setMediaItem( mediaItem: MediaItem ) {
        TODO("Not yet implemented")
    }

    override fun setMediaItem(mediaItem: MediaItem, startPositionMs: Long) {
        TODO("Not yet implemented")
    }

    override fun setMediaItem(mediaItem: MediaItem, resetPosition: Boolean) {
        TODO("Not yet implemented")
    }

    override fun addMediaItem(mediaItem: MediaItem) {
        TODO("Not yet implemented")
    }

    override fun addMediaItems(mediaItems: MutableList<MediaItem>) {
        TODO("Not yet implemented")
    }

    override fun addMediaItems(index: Int, mediaItems: MutableList<MediaItem>) {
        TODO("Not yet implemented")
    }

    override fun moveMediaItems(fromIndex: Int, toIndex: Int, newIndex: Int) {
        TODO("Not yet implemented")
    }

    override fun removeMediaItem(index: Int) {
        TODO("Not yet implemented")
    }

    override fun removeMediaItems(fromIndex: Int, toIndex: Int) {
        TODO("Not yet implemented")
    }

    override fun isCommandAvailable(command: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun canAdvertiseSession(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAvailableCommands(): Player.Commands {
        TODO("Not yet implemented")
    }

    override fun prepare() {}

    override fun getPlaybackSuppressionReason(): Int {
        TODO("Not yet implemented")
    }

    override fun getPlayerError(): PlaybackException? {
        TODO("Not yet implemented")
    }

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getShuffleModeEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isLoading(): Boolean {
        TODO("Not yet implemented")
    }

    override fun seekToDefaultPosition() {
        TODO("Not yet implemented")
    }

    override fun seekToDefaultPosition(mediaItemIndex: Int) {
        TODO("Not yet implemented")
    }

    override fun seekTo(positionMs: Long) {
        TODO("Not yet implemented")
    }

    override fun seekTo(mediaItemIndex: Int, positionMs: Long) {
        TODO("Not yet implemented")
    }

    override fun getSeekBackIncrement(): Long {
        TODO("Not yet implemented")
    }

    override fun seekBack() {
        TODO("Not yet implemented")
    }

    override fun getSeekForwardIncrement(): Long {
        TODO("Not yet implemented")
    }

    override fun seekForward() {
        TODO("Not yet implemented")
    }

    override fun hasPrevious(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasPreviousWindow(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasPreviousMediaItem(): Boolean {
        TODO("Not yet implemented")
    }

    override fun previous() {
        TODO("Not yet implemented")
    }

    override fun seekToPreviousWindow() {
        TODO("Not yet implemented")
    }

    override fun seekToPreviousMediaItem() {
        TODO("Not yet implemented")
    }

    override fun getMaxSeekToPreviousPosition(): Long {
        TODO("Not yet implemented")
    }

    override fun hasNext(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasNextWindow(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasNextMediaItem(): Boolean {
        TODO("Not yet implemented")
    }

    override fun next() {
        TODO("Not yet implemented")
    }

    override fun seekToNextWindow() {
        TODO("Not yet implemented")
    }

    override fun seekToNextMediaItem() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun release() {
        TODO("Not yet implemented")
    }

    override fun getCurrentTracks(): Tracks {
        TODO("Not yet implemented")
    }

    override fun getTrackSelectionParameters(): TrackSelectionParameters {
        TODO("Not yet implemented")
    }

    override fun setTrackSelectionParameters(parameters: TrackSelectionParameters) {
        TODO("Not yet implemented")
    }

    override fun getMediaMetadata(): MediaMetadata {
        TODO("Not yet implemented")
    }

    override fun getPlaylistMetadata(): MediaMetadata {
        TODO("Not yet implemented")
    }

    override fun setPlaylistMetadata(mediaMetadata: MediaMetadata) {
        TODO("Not yet implemented")
    }

    override fun getCurrentManifest(): Any? {
        TODO("Not yet implemented")
    }

    override fun getCurrentTimeline(): Timeline {
        TODO("Not yet implemented")
    }

    override fun getCurrentPeriodIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun getCurrentWindowIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun getNextWindowIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun getNextMediaItemIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun getPreviousWindowIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun getPreviousMediaItemIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun getCurrentPosition(): Long {
        TODO("Not yet implemented")
    }

    override fun getBufferedPosition(): Long {
        TODO("Not yet implemented")
    }

    override fun getBufferedPercentage(): Int {
        TODO("Not yet implemented")
    }

    override fun getTotalBufferedDuration(): Long {
        TODO("Not yet implemented")
    }

    override fun isCurrentWindowDynamic(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCurrentMediaItemDynamic(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCurrentWindowLive(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCurrentMediaItemLive(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getCurrentLiveOffset(): Long {
        TODO("Not yet implemented")
    }

    override fun isCurrentWindowSeekable(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCurrentMediaItemSeekable(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isPlayingAd(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getCurrentAdGroupIndex(): Int {
        TODO("Not yet implemented")
    }

    override fun getCurrentAdIndexInAdGroup(): Int {
        TODO("Not yet implemented")
    }

    override fun getContentDuration(): Long {
        TODO("Not yet implemented")
    }

    override fun getContentPosition(): Long {
        TODO("Not yet implemented")
    }

    override fun getContentBufferedPosition(): Long {
        TODO("Not yet implemented")
    }

    override fun getAudioAttributes(): AudioAttributes {
        TODO("Not yet implemented")
    }

    override fun setVolume(volume: Float) {
        TODO("Not yet implemented")
    }

    override fun getVolume(): Float {
        TODO("Not yet implemented")
    }

    override fun clearVideoSurface() {
        TODO("Not yet implemented")
    }

    override fun clearVideoSurface(surface: Surface?) {
        TODO("Not yet implemented")
    }

    override fun setVideoSurface(surface: Surface?) {
        TODO("Not yet implemented")
    }

    override fun setVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {
        TODO("Not yet implemented")
    }

    override fun clearVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {
        TODO("Not yet implemented")
    }

    override fun setVideoSurfaceView(surfaceView: SurfaceView?) {
        TODO("Not yet implemented")
    }

    override fun clearVideoSurfaceView(surfaceView: SurfaceView?) {
        TODO("Not yet implemented")
    }

    override fun setVideoTextureView(textureView: TextureView?) {
        TODO("Not yet implemented")
    }

    override fun clearVideoTextureView(textureView: TextureView?) {
        TODO("Not yet implemented")
    }

    override fun getVideoSize(): VideoSize {
        TODO("Not yet implemented")
    }

    override fun getSurfaceSize(): Size {
        TODO("Not yet implemented")
    }

    override fun getCurrentCues(): CueGroup {
        TODO("Not yet implemented")
    }

    override fun getDeviceInfo(): DeviceInfo {
        TODO("Not yet implemented")
    }

    override fun getDeviceVolume(): Int {
        TODO("Not yet implemented")
    }

    override fun isDeviceMuted(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setDeviceVolume(volume: Int) {
        TODO("Not yet implemented")
    }

    override fun setDeviceVolume(volume: Int, flags: Int) {
        TODO("Not yet implemented")
    }

    override fun increaseDeviceVolume() {
        TODO("Not yet implemented")
    }

    override fun increaseDeviceVolume(flags: Int) {
        TODO("Not yet implemented")
    }

    override fun decreaseDeviceVolume() {
        TODO("Not yet implemented")
    }

    override fun decreaseDeviceVolume(flags: Int) {
        TODO("Not yet implemented")
    }

    override fun setDeviceMuted(muted: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setDeviceMuted(muted: Boolean, flags: Int) {
        TODO("Not yet implemented")
    }

    override fun setAudioAttributes(audioAttributes: AudioAttributes, handleAudioFocus: Boolean) {
        TODO("Not yet implemented")
    }
}