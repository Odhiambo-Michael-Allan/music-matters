package com.odesa.musicMatters.core.common.media

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.os.Bundle
import android.os.ConditionVariable
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.odesa.musicMatters.core.common.MusicMattersMediaNotificationProvider
import com.odesa.musicMatters.core.common.R
import com.odesa.musicMatters.core.common.connection.SONG_ID_KEY
import com.odesa.musicMatters.core.common.di.CommonDiModule
import com.odesa.musicMatters.core.common.media.library.BrowseTree
import com.odesa.musicMatters.core.common.media.library.LocalMusicSource
import com.odesa.musicMatters.core.common.media.library.MEDIA_SEARCH_SUPPORTED
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_BROWSABLE_ROOT
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_TRACKS_ROOT
import com.odesa.musicMatters.core.common.media.library.MusicSource
import com.odesa.musicMatters.core.data.di.DataDiModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.Executors

/**
 * Service for browsing the catalogue and receiving  a [MediaController] from the app's UI
 * and other apps that will to play music via MUSICALLY.
 *
 * Browsing begins with the method [MusicService.MusicServiceCallback.onGetLibraryRoot], and
 * continues in the callback [MusicService.MusicServiceCallback.onGetChildren].
 *
 */

@UnstableApi
class MusicService : MediaLibraryService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope( Dispatchers.Main + serviceJob )
    private lateinit var mediaSession: MediaLibrarySession
    private var currentMediaItemIndex: Int = 0
    private lateinit var musicSource: MusicSource
    private lateinit var dataDiModule: DataDiModule
    private lateinit var commonDiModule: CommonDiModule
    private val headsetReceiverIntentFilter = IntentFilter( Intent.ACTION_HEADSET_PLUG )

    private val catalogueRootMediaItem: MediaItem by lazy {
        MediaItem.Builder()
            .setMediaId( MUSIC_MATTERS_BROWSABLE_ROOT )
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setFolderType( MediaMetadata.FOLDER_TYPE_ALBUMS )
                    .setIsPlayable( false )
                    .build() )
            .build()
    }

    /**
     * This must by 'by lazy' because [MusicSource] won't initially be ready. Use
     * [callWhenMusicSourceIsReady] to be sure it is safely ready for usage.
     */
    private val browseTree: BrowseTree by lazy {
        BrowseTree( musicSource )
    }

    private val executorService by lazy {
        MoreExecutors.listeningDecorator( Executors.newSingleThreadExecutor() )
    }

    private val musicallyAudioAttributes = AudioAttributes.Builder()
        .setContentType( C.AUDIO_CONTENT_TYPE_MUSIC )
        .setUsage( C.USAGE_MEDIA )
        .build()

    private val playerListener = PlayerEventListener()

    /**
     * Configure ExoPlayer to handle audio focus for use. See [ExoPlayer.Builder.setAudioAttributes]
     * for details.
     */
    private val exoPlayer: Player by lazy {
        val player = ExoPlayer.Builder( this ).build().apply {
            setAudioAttributes( musicallyAudioAttributes, true )
            setHandleAudioBecomingNoisy( true )
            addListener( playerListener )
        }
        player.addAnalyticsListener( EventLogger(  "exoplayer-music-matters" ) )
        player
    }

    private val replaceableForwardingPlayer: ReplaceableForwardingPlayer by lazy {
        ReplaceableForwardingPlayer( exoPlayer )
    }

    private val sessionCommands =
        MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon()
            .also { builder ->
                builder.add( SessionCommand( CUSTOM_COMMAND_DELETE_SONG, Bundle.EMPTY ) )
            }.build()

    /** @return the {@link MediaLibrarySessionCallback} to be used to build the media session */
    @OptIn(UnstableApi::class)
    fun getCallback(): MediaLibrarySession.Callback {
        return MusicServiceCallback()
    }

    private val headsetReceiver = object : BroadcastReceiver() {
        override fun onReceive( context: Context?, intent: Intent? ) {
            intent?.action?.let { action ->
                if ( action == Intent.ACTION_HEADSET_PLUG ) {
                    when ( intent.getIntExtra( "state", -1 ) ) {
                        0 -> {
                            Timber.tag(TAG).d( "HEADSET DISCONNECTED" )
                            if ( !dataDiModule.settingsRepository.pauseOnHeadphonesDisconnect.value )
                                commonDiModule.musicServiceConnection.play()
                        }
                        1 -> {
                            Timber.tag(TAG).d( "HEADSET CONNECTED" )
                            if ( dataDiModule.settingsRepository.playOnHeadphonesConnect.value )
                                commonDiModule.musicServiceConnection.play()
                        }
                    }
                }
            }
        }

    }

    private var mediaStoreObserverHandlerThread: HandlerThread? = null
    private lateinit var mediaStoreObserver: ContentObserver

    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        dataDiModule = DataDiModule.getInstance(
            context = applicationContext,
            dispatcher = Dispatchers.Main
        )
        commonDiModule = CommonDiModule.getInstance(
            context = applicationContext,
            playlistRepository = dataDiModule.playlistRepository,
            settingsRepository = dataDiModule.settingsRepository,
            songsAdditionalMetadataRepository = dataDiModule.songsAdditionalMetadataRepository
        )
        mediaSession = with (
            MediaLibrarySession.Builder(this, replaceableForwardingPlayer, getCallback() )
        ) {
            setId( packageName )
            packageManager?.getLaunchIntentForPackage( packageName )?.let { sessionIntent ->
                setSessionActivity(
                    PendingIntent.getActivity(
                        this@MusicService,
                        0,
                        sessionIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
            build()
        }

        musicSource = LocalMusicSource(
            context = applicationContext,
            songsAdditionalMetadataRepository = dataDiModule.songsAdditionalMetadataRepository
        )
        MediaPermissionsManager.checkForPermissions( applicationContext )
        serviceScope.launch {
            MediaPermissionsManager.hasAllRequiredPermissions.collect {
                Timber.tag( TAG ).d( "HAS ALL REQUIRED PERMISSIONS: $it" )
                if ( it ) musicSource.load()
            }
        }
        setMediaNotificationProvider( MusicMattersMediaNotificationProvider( applicationContext ) )
        registerHeadsetEvents()

        mediaStoreObserverHandlerThread = HandlerThread( "MediaStoreObserverHandlerThread" )
        mediaStoreObserverHandlerThread?.start()
        mediaStoreObserver = MediaStoreObserver(
            Handler( mediaStoreObserverHandlerThread!!.looper )
        ) {
            serviceScope.launch {
                Timber.tag( TAG ).d( "MEDIA STORE CONTENT CHANGED" )
                musicSource.load()
                browseTree.buildTree()
                val intent = Intent( MEDIA_STORE_UPDATED_INTENT )
                sendBroadcast( intent )
            }
        }
        contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            mediaStoreObserver
        )
    }

    private fun registerHeadsetEvents() {
        registerReceiver( headsetReceiver, headsetReceiverIntentFilter )
    }

    override fun onGetSession( controllerInfo: MediaSession.ControllerInfo ): MediaLibrarySession? {
        return mediaSession
    }

    /**
     * Called when swiping the activity away from recents
     */
    override fun onTaskRemoved( rootIntent: Intent? ) {
        super.onTaskRemoved( rootIntent )
    }

    override fun onUnbind( intent: Intent? ): Boolean {
        return super.onUnbind( intent )
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaSession()
        unregisterReceiver( headsetReceiver )
        contentResolver.unregisterContentObserver( mediaStoreObserver )
        mediaStoreObserverHandlerThread?.quitSafely()
    }

    private fun releaseMediaSession() {
        mediaSession.run {
            release()
            if ( player.playbackState != Player.STATE_IDLE ) {
                player.removeListener( playerListener )
                player.release()
            }
        }
        // Cancel coroutines when the service is going away.
        serviceJob.cancel()
    }

    /**
     * Returns a future that executes the action when the music source is ready. This may be an
     * immediate execution if the music source is ready, or a deferred asynchronous execution if the
     * music source is still loading.
     *
     * @param action The function to be called when the music source is ready.
     */
    private fun <T> callWhenMusicSourceIsReady( action: () -> T ): ListenableFuture<T> {
        val conditionVariable = ConditionVariable()
        return if ( musicSource.whenReady( openWhenReady( conditionVariable ) ) ) {
            Futures.immediateFuture( action() )
        } else {
            executorService.submit<T> {
                conditionVariable.block()
                action()
            }
        }
    }

    /** Returns a function that opens the condition variable when called. */
    private fun openWhenReady( conditionVariable: ConditionVariable): ( Boolean ) -> Unit = {
        val successfullyInitialized = it
        if ( !successfullyInitialized ) {
            Timber.tag( TAG ).d( "Loading music failed" )
        }
        conditionVariable.open()
    }

    @UnstableApi
    open inner class MusicServiceCallback : MediaLibrarySession.Callback {

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            return MediaSession.ConnectionResult.AcceptedResultBuilder( session )
                .setAvailableSessionCommands( sessionCommands )
                .build()
        }

        @UnstableApi
        override fun onGetLibraryRoot(
            session: MediaLibrarySession, browser: MediaSession.ControllerInfo, params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val isKnownCaller = true
            val rootExtras = Bundle().apply {
                putBoolean( MEDIA_SEARCH_SUPPORTED, true )
                putBoolean( CONTENT_STYLE_SUPPORTED, true )
                putInt( CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_GRID )
                putInt( CONTENT_STYLE_PLAYABLE_HINT, CONTENT_STYLE_LIST )
            }
            val libraryParams = LibraryParams.Builder().setExtras( rootExtras ).build()
            val rootMediaItem = if ( !isKnownCaller ) {
                MediaItem.EMPTY
            }  else {
                catalogueRootMediaItem
            }
            return Futures.immediateFuture( LibraryResult.ofItem( rootMediaItem, libraryParams ) )
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            return callWhenMusicSourceIsReady {
                val requestedChildren = browseTree[ parentId ]
                LibraryResult.ofItemList(
                    requestedChildren ?: ImmutableList.of(),
                    LibraryParams.Builder().build()
                )
            }
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
            return callWhenMusicSourceIsReady {
                LibraryResult.ofItem(
                    browseTree.getMediaItemById( mediaId ) ?: EMPTY_MEDIA_ITEM,
                    LibraryParams.Builder().build()
                )
            }
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            return callWhenMusicSourceIsReady {
                mediaItems.map { browseTree.getMediaItemById( it.mediaId )!! }.toMutableList()
            }
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            Timber.tag( TAG ).d( "RECEIVED CUSTOM COMMAND ${customCommand.customAction}" )
            if ( customCommand.customAction == CUSTOM_COMMAND_DELETE_SONG ) {
                val extras = customCommand.customExtras
                extras.getString( SONG_ID_KEY )?.let {
                    browseTree.deleteMediaItemWithId( it )
                    browseTree.buildTree()
                }
                return Futures.immediateFuture(
                    SessionResult( SessionResult.RESULT_SUCCESS )
                )
            }
            return Futures.immediateFuture(
                SessionResult( SessionResult.RESULT_ERROR_NOT_SUPPORTED ) )
        }

        override fun onPlaybackResumption(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
            val currentlyPlayingSongId = dataDiModule.settingsRepository.currentlyPlayingSongId.value
            val songIdsInCurrentQueue = dataDiModule.playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds
            val mediaItems = browseTree[ MUSIC_MATTERS_TRACKS_ROOT ]
                ?.filter { songIdsInCurrentQueue.contains( it.mediaId ) } ?: emptyList()
            return Futures.immediateFuture(
                MediaSession.MediaItemsWithStartPosition(
                    mediaItems,
                    mediaItems.indexOfFirst { it.mediaId == currentlyPlayingSongId },
                    0L
                )
            )
        }
    }

    /** Listen for events from ExoPlayer */
    private inner class PlayerEventListener : Player.Listener {
        override fun onEvents( player: Player, events: Player.Events ) {
            if ( events.contains( Player.EVENT_POSITION_DISCONTINUITY )
                || events.contains( Player.EVENT_MEDIA_ITEM_TRANSITION )
                || events.contains( Player.EVENT_PLAY_WHEN_READY_CHANGED ) ) {
                currentMediaItemIndex = player.currentMediaItemIndex
            }
        }

        override fun onMediaItemTransition( mediaItem: MediaItem?, reason: Int ) {
            super.onMediaItemTransition( mediaItem, reason )
            Timber.tag( TAG ).d( "MEDIA ITEM TRANSITION. ID: ${mediaItem?.mediaId}" )
            mediaItem?.let {
                serviceScope.launch {
                    dataDiModule.playlistRepository.apply {
                        addToMostPlayedPlaylist( it.mediaId )
                        addToRecentlyPlayedSongsPlaylist( it.mediaId )
                    }
                    dataDiModule.settingsRepository.saveCurrentlyPlayingSongId( it.mediaId )
                }
            }
        }

        override fun onPlayerError( error: PlaybackException ) {
            var message = R.string.generic_error
            Timber.tag( TAG ).d( "Player error: ${error.errorCodeName} ( ${error.errorCode} )" )
            if ( error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS
                || error.errorCode == PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ) {
                message = R.string.error_media_not_found
            }
            Toast.makeText(
                applicationContext,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

/** Content styling constants */
private const val CONTENT_STYLE_BROWSABLE_HINT = "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"
private const val CONTENT_STYLE_PLAYABLE_HINT = "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT"
private const val CONTENT_STYLE_SUPPORTED = "android.media.browse.CONTENT_STYLE_SUPPORTED"
private const val CONTENT_STYLE_LIST = 1
private const val CONTENT_STYLE_GRID = 2

const val CUSTOM_COMMAND_DELETE_SONG = "com.odesa.musicmatters.delete_song"
private const val TAG = "MUSIC SERVICE TAG"
val EMPTY_MEDIA_ITEM = MediaItem.Builder()
    .setMediaId( UUID.randomUUID().toString() )
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setIsBrowsable( false )
            .setIsPlayable( false )
            .build()
    ).build()

const val MEDIA_STORE_UPDATED_INTENT = "MEDIA_STORE_UPDATED_INTENT"