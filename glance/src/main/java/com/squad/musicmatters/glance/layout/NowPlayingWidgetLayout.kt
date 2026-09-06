package com.squad.musicmatters.glance.layout

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.components.SquareIconButton
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.glance.R

@Composable
internal fun NowPlayingWidgetLayout(
    modifier: GlanceModifier = GlanceModifier,
    currentlyPlayingSongArtworkBitmap: Bitmap?,
    currentlyPlayingSong: Song?,
    isPlaying: Boolean,
    shuffle: Boolean,
    loopMode: LoopMode,
) {

    val launchMainActivityIntent = Intent( Intent.ACTION_VIEW ).apply {
        data = "musicmatters://foryou".toUri()
    }

    Row(
        modifier = modifier
            .padding( 0.dp, 8.dp ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        currentlyPlayingSongArtworkBitmap?.let {
            Image(
                provider = ImageProvider( it ),
                contentDescription = "song-artwork",
                modifier = GlanceModifier
                    .size( 78.dp, 80.dp )
                    .clickable( actionStartActivity( launchMainActivityIntent ) ),
            )
        } ?: run {
            SquareIconButton(
                imageProvider = ImageProvider( R.drawable.glance_music_note ),
                contentDescription = "song-artwork",
                backgroundColor = GlanceTheme.colors.secondaryContainer,
                contentColor = GlanceTheme.colors.onSecondaryContainer,
                modifier = GlanceModifier.size( 78.dp, 80.dp ),
                onClick = { actionStartActivity( launchMainActivityIntent ) }
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
                    color = GlanceTheme.colors.onSurface,
                ),
                maxLines = 1,
            )
            Text(
                text = currentlyPlayingSong?.artist ?: "",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onSurface,
                ),
                maxLines = 1,
            )
            PlayerControlsToolBarLayout(
                isPlaying = isPlaying,
                shuffle = shuffle,
                loopMode = loopMode,
            )
        }
    }
}


@OptIn( ExperimentalGlancePreviewApi::class )
@Preview( widthDp = 330, heightDp = 128 )
@Preview( widthDp = 256, heightDp = 128 )
@Composable
private fun MediumWidgetLayoutPreview() {
    NowPlayingWidgetLayout(
        modifier = GlanceModifier.fillMaxSize(),
        currentlyPlayingSong = Song(
            id = "id4",
            mediaStoreId = 0,
            title = "Dear Boy",
            artist = "Michael Jackson",
            duration = 0L,
            size = 0L,
            dateModified = 0,
            path = "path",
            trackNumber = null,
            year = null,
            albumTitle = "albumTitle",
            composer = null,
            artworkUri = "artworkUri",
            artistId = 0,
            albumId = 0,
            mediaUri = "",
        ),
        shuffle = false,
        loopMode = LoopMode.Song,
        isPlaying = true,
        currentlyPlayingSongArtworkBitmap = null,
    )
}