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
import androidx.compose.runtime.produceState
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
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.glance.R
import com.squad.musicmatters.glance.data.GlanceSong
import com.squad.musicmatters.glance.data.GlanceUiModel
import com.squad.musicmatters.glance.layout.MediumWidgetLayout
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

        val modelFlow = entryPoint.glanceRepository().getGlanceUiModel()

        provideContent {
            val glanceUiModel = modelFlow.collectAsState(
                initial = GlanceUiModel(
                    isPlaying = false,
                    shuffle = false,
                    currentlyPlayingSong = null,
                    loopMode = LoopMode.None,
                    songs = emptyList(),
                    currentlyPlayingSongIsFavorite = false,
                )
            )
            val currentlyPlayingSong = glanceUiModel.value.currentlyPlayingSong
            val context = LocalContext.current

            val artworkBitmap by produceState<Bitmap?>(
                initialValue = null,
                key1 = currentlyPlayingSong?.artworkUri
            ) {
                val uri = currentlyPlayingSong?.artworkUri?.toUri()
                value = uri?.let { context.loadBitmapFromUri( it ) }
            }

            GlanceTheme {
                MediumWidget(
                    currentlyPlayingSongArtworkBitmap = artworkBitmap,
                    glanceUiModel = glanceUiModel.value,
                )
            }
        }
    }

}

@Composable
private fun MediumWidget(
    currentlyPlayingSongArtworkBitmap: Bitmap?,
    glanceUiModel: GlanceUiModel
) {
    Scaffold(
        modifier = GlanceModifier.fillMaxSize(),
    ) {
        MediumWidgetLayout(
            modifier = GlanceModifier.fillMaxSize(),
            currentlyPlayingSongArtworkBitmap = currentlyPlayingSongArtworkBitmap,
            currentlyPlayingSong = glanceUiModel.currentlyPlayingSong,
            isPlaying = glanceUiModel.isPlaying,
            loopMode = glanceUiModel.loopMode,
            shuffle = glanceUiModel.shuffle,
        )
    }
}

@OptIn( ExperimentalGlancePreviewApi::class )
@Preview( widthDp = 330, heightDp = 128 )
@Preview( widthDp = 256, heightDp = 128 )
@Composable
private fun MediumWidgetPreview() {
    MediumWidget(
        currentlyPlayingSongArtworkBitmap = null,
        glanceUiModel = GlanceUiModel(
            isPlaying = true,
            loopMode = LoopMode.Song,
            shuffle = true,
            currentlyPlayingSong = GlanceSong(
                id = "id1",
                mediaStoreId = 0,
                title = "You're On ( feat. Kyan )",
                artist = "Michael Jackson",
            ),
            currentlyPlayingSongIsFavorite = false,
            songs = listOf(
                Song(
                    id = "id1",
                    mediaStoreId = 0,
                    mediaUri = "Uri.EMPTY",
                    title = "You're On ( feat. Kyan )",
                    albumId = 0L,
                    albumTitle = "D",
                    artist = "A - Michael Jackson",
                    artworkUri = "",
                    composer = "A,B",
                    dateModified = 354L,
                    duration = 60L,
                    trackNumber = 324,
                    year = 2022,
                    size = 1L,
                    path = "/path/to/song/7",
                    artistId = 0,
                ),
                Song(
                    id = "id2",
                    mediaStoreId = 0,
                    mediaUri = "Uri.EMPTY",
                    title = "Silk Music Showcase 07 ( Mixed by Jacob Henry & Tom Fall )",
                    albumTitle = "C",
                    artist = "B - Michael Jackson",
                    artworkUri = null,
                    composer = "B,C",
                    dateModified = 754L,
                    albumId = 0L,
                    duration = 4L,
                    trackNumber = 235,
                    year = 2002,
                    size = 2L,
                    path = "/path/to/song/8",
                    artistId = 0,
                ),
                Song(
                    id = "id3",
                    mediaStoreId = 0,
                    mediaUri = "Uri.EMPTY",
                    title = "Ric Flair Drip ( with Metro Boomin )",
                    albumTitle = "B",
                    artist = "C - Michael Jackson",
                    artworkUri = null,
                    composer = "C,D",
                    dateModified = 7976L,
                    albumId = 0L,
                    duration = 7L,
                    trackNumber = 443,
                    year = 2007,
                    size = 3L,
                    path = "/path/to/song/6",
                    artistId = 0,
                ),
                Song(
                    id = "id4",
                    mediaStoreId = 0,
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
                Song(
                    id = "id5",
                    mediaStoreId = 0,
                    mediaUri = "Uri.EMPTY",
                    title = "The Days",
                    albumTitle = "<unknown>",
                    artist = "E - Michael Jackson",
                    artworkUri = null,
                    composer = null,
                    dateModified = 34245L,
                    albumId = 0L,
                    duration = 89L,
                    trackNumber =134,
                    year = 1990,
                    size = 5L,
                    path = "/path/to/song/5",
                    artistId = 0,
                ),
            ).map {
                GlanceSong(
                    id = it.id,
                    mediaStoreId = it.mediaStoreId,
                    title = it.title,
                    artist = "A - Michael Jackson",
                )
            }
        )
    )
}


