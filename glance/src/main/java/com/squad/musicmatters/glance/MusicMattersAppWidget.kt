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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
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
import androidx.glance.appwidget.updateAll
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
import androidx.glance.unit.ColorProvider

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
            val currentlyPlayingSong by currentlyPlayingSongFlow.collectAsState(initial = null)
            val context = LocalContext.current

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
                Content(
                    currentlyPlayingSong = currentlyPlayingSong,
                    currentlyPlayingSongBitmap = artworkBitmap
                )
            }
        }
    }
}

@Composable
private fun Content(
    currentlyPlayingSong: Song?,
    currentlyPlayingSongBitmap: Bitmap?,
) {
    Scaffold(
        modifier = GlanceModifier.fillMaxSize(),
        backgroundColor = GlanceTheme.colors.widgetBackground,
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding( 8.dp ),
            verticalAlignment = Alignment.CenterVertically
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
                modifier = GlanceModifier.defaultWeight()
            ) {
                Text(
                    text = currentlyPlayingSong?.title ?: "No queued tracks",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                )
                Text(
                    text = currentlyPlayingSong?.artist ?: "",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontWeight = FontWeight.Normal,
                    ),
                    maxLines = 1,
                )
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        provider = ImageProvider( R.drawable.round_repeat_24 ),
                        contentDescription = "repeat-mode",
                        colorFilter = ColorFilter.tint( GlanceTheme.colors.onSurface ),
                        modifier = GlanceModifier
                            .size( 20.dp )
                            .clickable( actionRunCallback<TogglePlayPauseAction>() )
                    )
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
                    Image(
                        provider = ImageProvider( R.drawable.round_shuffle_24 ),
                        contentDescription = "shuffle",
                        colorFilter = ColorFilter.tint( GlanceTheme.colors.onSurface ),
                        modifier = GlanceModifier
                            .size( 20.dp )
                            .clickable( actionRunCallback<TogglePlayPauseAction>() )
                    )
                }
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
    Content(
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
        currentlyPlayingSongBitmap = createBitmap( 100, 100 )
            .apply { eraseColor( android.graphics.Color.RED ) },
    )
}