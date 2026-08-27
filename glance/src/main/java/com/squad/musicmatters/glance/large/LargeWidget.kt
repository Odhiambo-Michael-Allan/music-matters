package com.squad.musicmatters.glance.large

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.media3.common.util.UnstableApi
import coil3.Bitmap
import com.squad.musicmatters.core.media.media.MusicService
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.glance.data.GlanceSong
import com.squad.musicmatters.glance.data.GlanceUiModel
import com.squad.musicmatters.glance.di.GlanceModuleEntryPoint
import com.squad.musicmatters.glance.layout.MediumWidgetLayout
import com.squad.musicmatters.glance.layout.NoOpAction
import com.squad.musicmatters.glance.layout.startMusicService
import com.squad.musicmatters.glance.loadBitmapFromUri
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class LargeWidget : GlanceAppWidget() {

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
            val model = modelFlow.collectAsState(
                initial = GlanceUiModel(
                    isPlaying = false,
                    shuffle = false,
                    currentlyPlayingSong = null,
                    loopMode = LoopMode.None,
                    songs = emptyList(),
                    currentlyPlayingSongIsFavorite = false,
                )
            )
            val currentlyPlayingSong = model.value.currentlyPlayingSong
            val songs = model.value.songs
            val context = LocalContext.current

            // Load bitmap asynchronously inside the composition scope when song changes
            var artworkBitmap by remember( currentlyPlayingSong?.artworkUri ) {
                mutableStateOf<android.graphics.Bitmap?>( null )
            }

            LaunchedEffect( currentlyPlayingSong?.artworkUri ) {
                val uri = currentlyPlayingSong?.artworkUri?.toUri()
                artworkBitmap = if ( uri != null ) {
                    context.loadBitmapFromUri( uri )
                } else null
            }

            GlanceTheme {
                LargeWidget(
                    currentlyPlayingSongArtworkBitmap = artworkBitmap,
                    glanceUiModel = model.value
                )
            }
        }
    }

}

@androidx.annotation.OptIn( UnstableApi::class )
@Composable
private fun LargeWidget(
    currentlyPlayingSongArtworkBitmap: Bitmap?,
    glanceUiModel: GlanceUiModel,
) {

    val context = LocalContext.current

    Scaffold(
        modifier = GlanceModifier.fillMaxSize(),
    ) {
        Column (
            modifier = GlanceModifier.padding( 0.dp, 8.dp )
        ) {
            MediumWidgetLayout(
                currentlyPlayingSongArtworkBitmap = currentlyPlayingSongArtworkBitmap,
                currentlyPlayingSong = glanceUiModel.currentlyPlayingSong,
                isPlaying = glanceUiModel.isPlaying,
                loopMode = glanceUiModel.loopMode,
                shuffle = glanceUiModel.shuffle,
            )
            Spacer( modifier = GlanceModifier.height( 4.dp ) )
            LazyColumn(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background( GlanceTheme.colors.secondaryContainer )
                    .cornerRadius( 12.dp )
            ) {
                items(
                    items = glanceUiModel.songs,
                    itemId = { it.mediaStoreId }
                ) {
                    val textColor = if ( it.id == glanceUiModel.currentlyPlayingSong?.id ) {
                        GlanceTheme.colors.primary
                    } else {
                        GlanceTheme.colors.onSurface
                    }
                    Column(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding( 12.dp, 8.dp )
                            .clickable(
                                context.startMusicService(
                                    intentAction = MusicService.ACTION_PLAY,
                                    intentExtras = Pair( MusicService.SONG_ID_INTENT_KEY, it.id )
                                )
                            )
                    ) {
                        Text(
                            text = it.title,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = textColor,
                            ),
                            maxLines = 1,
                        )
                        Text(
                            text = it.artist,
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = textColor,
                            ),
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@OptIn( ExperimentalGlancePreviewApi::class )
@Preview( widthDp = 330, heightDp = 400 )
@Preview( widthDp = 256, heightDp = 400 )
@Composable
private fun LargePreview() {
    LargeWidget(
        currentlyPlayingSongArtworkBitmap = null,
        glanceUiModel = GlanceUiModel(
            isPlaying = true,
            loopMode = LoopMode.Song,
            shuffle = true,
            currentlyPlayingSong = GlanceSong(
                id = "id1",
                mediaStoreId = 0,
                title = "You're On ( feat. Kyan )",
                artist = "A - Michael Jackson",
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
        ),

    )
}