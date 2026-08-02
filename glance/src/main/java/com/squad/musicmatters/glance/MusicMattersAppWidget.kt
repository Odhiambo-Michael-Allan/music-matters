package com.squad.musicmatters.glance

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import androidx.glance.text.Text
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.glance.di.GlanceModuleEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Spacer
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.request.transformations
import coil3.toBitmap
import coil3.transform.RoundedCornersTransformation
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.SquareIconButton
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview

class MusicMattersAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            GlanceModuleEntryPoint::class.java
        )

        val songsRepository = entryPoint.songsRepository()
        val userDataRepository = entryPoint.userDataRepository()

        // 1. Build the cold flow
        val currentlyPlayingSongFlow = userDataRepository.userData
            .map { it.currentlyPlayingSongId }
            .flatMapLatest { id ->
                songsRepository.fetchSongs().map { songs -> songs.find { it.id == id } }
            }

        // 2. Go directly to provideContent!
        provideContent {
            val currentlyPlayingSong by currentlyPlayingSongFlow.collectAsState( initial = null )
            val context = LocalContext.current
            val size = LocalSize.current

            // Load bitmap asynchronously inside the composition scope when song changes
            var artworkBitmap by remember( currentlyPlayingSong?.artworkUri ) {
                mutableStateOf<Bitmap?>( null )
            }

            LaunchedEffect( currentlyPlayingSong?.artworkUri ) {
                val uri = currentlyPlayingSong?.artworkUri?.toUri()
                artworkBitmap = if ( uri != null ) {
                    loadBitmapFromUri( context, uri )
                } else null
            }

            GlanceTheme {
                when ( size ) {
                    mediumMode -> {
                        WidgetMediumLarge(
                            currentlyPlayingSong = currentlyPlayingSong,
                            currentlyPlayingSongBitmap = artworkBitmap,
                        )
                    }
                    largeMode -> {
                        WidgetMediumLarge(
                            currentlyPlayingSong = currentlyPlayingSong,
                            currentlyPlayingSongBitmap = artworkBitmap,
                            layoutIsLarge = true,
                        )
                    }
                }
            }
        }
    }

    companion object {
        private val mediumMode = DpSize(180.dp, 48.dp)
        private val largeMode = DpSize(300.dp, 48.dp)
    }

    /**
     * Define the supported sizes for this widget. The system will decide which one fits better
     * based on the available space.
     */
    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf( mediumMode, largeMode )
    )
}

@Composable
private fun WidgetMediumLarge(
    currentlyPlayingSong: Song?,
    currentlyPlayingSongBitmap: Bitmap?,
    layoutIsLarge: Boolean = false,
) {
    Scaffold(
        modifier = GlanceModifier.fillMaxSize(),
        backgroundColor = GlanceTheme.colors.widgetBackground,
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding( 8.dp ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            currentlyPlayingSongBitmap?.let {
                Image(
                    provider = ImageProvider( currentlyPlayingSongBitmap ),
                    contentDescription = "song-artwork",
                    modifier = GlanceModifier.size( 72.dp ),
                )
            } ?: run {
                SquareIconButton(
                    imageProvider = ImageProvider( R.drawable.round_music_note_24 ),
                    contentDescription = "song-artwork",
                    modifier = GlanceModifier.size( 72.dp ),
                    onClick = {}
                )
            }
            

            Spacer( modifier = GlanceModifier.width( 8.dp ) )

            Column(
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                Text(
                    text = currentlyPlayingSong?.title ?: "No queued tracks",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.secondary
                    ),
                    maxLines = 1,
                )
                Text(
                    text = currentlyPlayingSong?.artist ?: "",
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        color = GlanceTheme.colors.secondary
                    ),
                    maxLines = 1,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if ( layoutIsLarge ) {
                        Image(
                            provider = ImageProvider( R.drawable.round_repeat_24 ),
                            contentDescription = "repeat-mode",
                            colorFilter = ColorFilter.tint( GlanceTheme.colors.onSurface ),
                            modifier = GlanceModifier
                                .size( 25.dp )
                                .clickable( actionRunCallback<TogglePlayPauseAction>() )
                        )
                    }
                    Image(
                        provider = ImageProvider( R.drawable.round_skip_previous_24 ),
                        contentDescription = "skip-to-previous",
                        colorFilter = ColorFilter.tint( GlanceTheme.colors.onSurface ),
                        modifier = GlanceModifier
                            .size( 40.dp )
                            .clickable( actionRunCallback<TogglePlayPauseAction>() )
                    )
                    Image(
                        provider = ImageProvider( R.drawable.round_play_arrow_24 ),
                        contentDescription = "Play/Pause",
                        colorFilter = ColorFilter.tint( GlanceTheme.colors.onSurface ),
                        modifier = GlanceModifier
                            .size( 40.dp )
                            .clickable( actionRunCallback<TogglePlayPauseAction>() )
                    )
                    Image(
                        provider = ImageProvider( R.drawable.round_skip_next_24 ),
                        contentDescription = "skip-to-next",
                        colorFilter = ColorFilter.tint( GlanceTheme.colors.onSurface ),
                        modifier = GlanceModifier
                            .size( 40.dp )
                            .clickable( actionRunCallback<TogglePlayPauseAction>() )
                    )
                    if ( layoutIsLarge ) {
                        Image(
                            provider = ImageProvider( R.drawable.round_shuffle_24 ),
                            contentDescription = "shuffle",
                            colorFilter = ColorFilter.tint( GlanceTheme.colors.onSurface ),
                            modifier = GlanceModifier
                                .size( 25.dp )
                                .clickable( actionRunCallback<TogglePlayPauseAction>() )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetSmall(
    currentlyPlayingSongBitmap: Bitmap?,
) {
    Scaffold(
        modifier = GlanceModifier.fillMaxSize(),
        backgroundColor = GlanceTheme.colors.widgetBackground,
    ) {
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = GlanceModifier
                .size( 120.dp )
                .padding( 8.dp )
        ) {
            CircleIconButton(
                imageProvider = currentlyPlayingSongBitmap?.let { ImageProvider( it ) }
                    ?: ImageProvider( R.drawable.round_music_note_24 ),
                backgroundColor = GlanceTheme.colors.primaryContainer,
                contentDescription = "song-artwork",
                modifier = GlanceModifier.fillMaxSize(),
                onClick = {}
            )
            Row(
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                SquareIconButton(
                    imageProvider = ImageProvider( R.drawable.round_play_arrow_24 ),
                    contentDescription = "",
                    modifier = GlanceModifier.size( 42.dp ),
                    onClick = {}
                )
            }
        }
    }
}

private suspend fun loadBitmapFromUri(
    context: Context,
    uri: Uri,
    cornerRadiusPx: Float = 32f
): Bitmap? {
    val request = ImageRequest.Builder( context )
        .data( uri )
        .allowHardware( false ) // Required for Glance IPC
        .size( 256, 256 )
        .transformations( RoundedCornersTransformation( cornerRadiusPx ) )
        .build()

    val result = ImageLoader( context ).execute( request )
    return ( result as? SuccessResult )?.image?.toBitmap()
}

class TogglePlayPauseAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {}

}

@OptIn( ExperimentalGlancePreviewApi::class )
@Preview
@Composable
private fun ContentPreview() {
    WidgetMediumLarge(
        layoutIsLarge = true,
        currentlyPlayingSong = Song(
            id = "id4",
            mediaUri = "Uri.EMPTY",
            title = "Dear Boy",
            albumTitle = "A",
            artist = "D - Michael Jackson",
            artworkUri = null,
            composer = "D,E",
            dateModified = 200L,
            albumId = 0L,
            duration = 4L,
            trackNumber = 234,
            year = 2004,
            size = 4L,
            path = "/path/to/song/1",
            artistId = 0,
        ),
        currentlyPlayingSongBitmap = null,
//            createBitmap( 100, 100 )
//            .apply { eraseColor( android.graphics.Color.RED ) },
    )
}

@OptIn( ExperimentalGlancePreviewApi::class )
@Preview
@Composable
private fun WidgetSmallPreview() {
    WidgetSmall(
        currentlyPlayingSongBitmap = null,
    )
}