package com.squad.musicmatters.glance.medium

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
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
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Box
import androidx.glance.layout.height
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.semantics.contentDescription
import androidx.glance.semantics.semantics
import androidx.glance.unit.ColorProvider
import com.squad.musicmatters.glance.R
import com.squad.musicmatters.glance.layout.PlayerControlsToolBarLayout
import com.squad.musicmatters.glance.loadBitmapFromUri

class MediumWidget : GlanceAppWidget() {

    // Unlike the "Single" size mode, using "Exact" allows us to have better control over rendering in
    // different sizes. And, unlike the "Responsive" mode, it doesn't cause several views for each
    // supported size to be held in the widget host's memory.
    override val sizeMode: SizeMode = SizeMode.Exact

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
                    context.loadBitmapFromUri( uri )
                } else null
            }

            GlanceTheme {
                MediumWidget(
                    currentlyPlayingSong = currentlyPlayingSong,
                    currentlyPlayingSongBitmap = artworkBitmap,
                )
            }
        }
    }

}

@Composable
private fun MediumWidget(
    currentlyPlayingSong: Song?,
    currentlyPlayingSongBitmap: Bitmap?,
) {
    Scaffold(
        modifier = GlanceModifier.fillMaxSize(),
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding( 0.dp, 8.dp ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            currentlyPlayingSongBitmap?.let {
                Image(
                    provider = ImageProvider( currentlyPlayingSongBitmap ),
                    contentDescription = "song-artwork",
                    modifier = GlanceModifier.size( 78.dp, 80.dp ),
                )
            } ?: run {
                SquareIconButton(
                    imageProvider = ImageProvider( R.drawable.glance_music_note ),
                    contentDescription = "song-artwork",
                    backgroundColor = GlanceTheme.colors.secondaryContainer,
                    contentColor = GlanceTheme.colors.onSecondaryContainer,
                    modifier = GlanceModifier.size( 78.dp, 80.dp ),
                    onClick = {}
                )
            }
            

            Spacer( modifier = GlanceModifier.width( 12.dp ) )

            Column(
                modifier = GlanceModifier.fillMaxWidth().height( 80.dp ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentlyPlayingSong?.title ?: "No queued tracks",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.onPrimaryContainer
                    ),
                    maxLines = 1,
                )
                Text(
                    text = currentlyPlayingSong?.artist ?: "",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = GlanceTheme.colors.onPrimaryContainer
                    ),
                    maxLines = 1,
                )
                PlayerControlsToolBarLayout()
            }
        }
    }
}


@OptIn( ExperimentalGlancePreviewApi::class )
@Preview( widthDp = 330, heightDp = 128 )
@Preview( widthDp = 256, heightDp = 128 )
@Composable
private fun MediumWidgetCPreview() {
    MediumWidget(
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
