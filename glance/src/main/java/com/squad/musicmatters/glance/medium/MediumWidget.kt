package com.squad.musicmatters.glance.medium

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.fillMaxSize
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.glance.di.GlanceModuleEntryPoint
import dagger.hilt.android.EntryPointAccessors
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.glance.data.GlanceUiModel
import com.squad.musicmatters.glance.layout.MediumWidgetLayout
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
            currentlyPlayingSong = Song(
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
            currentlyPlayingSongIsFavorite = false,
        )
    )
}


