package com.squad.musicmatters.glance.small

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.coerceAtLeast
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
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.SquareIconButton
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import com.squad.musicmatters.glance.R
import com.squad.musicmatters.glance.di.GlanceModuleEntryPoint
import com.squad.musicmatters.glance.layout.NoOpAction
import com.squad.musicmatters.glance.layout.RectangularIconButton
import com.squad.musicmatters.glance.loadBitmapFromUri
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlin.ranges.coerceAtMost

class SmallWidget : GlanceAppWidget() {

    // Unlike the "Single" size mode, using "Exact" allows us to have better control over rendering
    // in different sizes. And, unlike the "Responsive" mode, it doesn't cause several views for
    // each supported size to be held in the widget host's memory.
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

        val currentlyPlayingSongFlow = userDataRepository.userData
            .map { it.currentlyPlayingSongId }
            .flatMapLatest { id ->
                songsRepository.fetchSongs().map { songs -> songs.find { it.id == id } }
            }

        provideContent {
            val currentlyPlayingSong by currentlyPlayingSongFlow.collectAsState( initial = null )
            val context = LocalContext.current

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
                SmallWidget( currentlyPlayingSongBitmap = artworkBitmap )
            }
        }
    }

}

@Composable
private fun SmallWidget(
    currentlyPlayingSongBitmap: Bitmap?,
) {

    val widgetSize = LocalSize.current
    // Since we use a non-rectangular background, we aren't able to fill the entire widget space,
    // however, we try to at least fill the space either horizontally or vertically.
    val backgroundSize = widgetSize.height.coerceAtMost( widgetSize.width )
    val backgroundModifier = GlanceModifier
        .size( backgroundSize )
        .appWidgetBackground()
    val iconSize = minOf(
        widgetSize.div( 4 ).width,
        widgetSize.div( 4 ).height
    )

    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = backgroundModifier
            .padding( 8.dp )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius( 300.dp )
                .clickable {}
        ) {
            currentlyPlayingSongBitmap?.let {
                Image(
                    provider = ImageProvider( it ),
                    contentDescription = null,
                    modifier = GlanceModifier.fillMaxSize()
                )
            } ?: run {
                RectangularIconButton(
                    iconImageProvider = ImageProvider( R.drawable.round_play_circle_outline_24 ),
                    contentDescription = "",
                    iconSize = iconSize.coerceAtLeast( 48.dp ),
                    roundedCornerShape = com.squad.musicmatters.glance.layout.RoundedCornerShape.MEDIUM,
                    backgroundColor = GlanceTheme.colors.widgetBackground,
                    contentColor = GlanceTheme.colors.onSecondaryContainer,
                    onClick = actionRunCallback<NoOpAction>(),
                    modifier = GlanceModifier.fillMaxSize()
                )
            }
        }
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            SquareIconButton(
                imageProvider = ImageProvider( R.drawable.round_play_arrow_24 ),
                contentDescription = "",
                backgroundColor = GlanceTheme.colors.secondaryContainer,
                contentColor = GlanceTheme.colors.onSecondaryContainer,
                onClick = {},
                modifier = GlanceModifier.size( iconSize.coerceAtLeast( 55.dp ) )
            )
        }
        Box(
            modifier = GlanceModifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            CircleIconButton(
                imageProvider = ImageProvider( R.drawable.glance_thumbs_up ),
                backgroundColor = GlanceTheme.colors.tertiaryContainer,
                contentColor = GlanceTheme.colors.onTertiaryContainer,
                contentDescription = "",
                modifier = GlanceModifier.size( iconSize.coerceAtLeast( 48.dp ) ),
                onClick = {}
            )
        }
    }
}

@OptIn( ExperimentalGlancePreviewApi::class )
@Preview( 200, 200 )
@Composable
private fun WidgetSmallPreview() {
    GlanceTheme {
        SmallWidget(
            currentlyPlayingSongBitmap = null,
        )
    }
}