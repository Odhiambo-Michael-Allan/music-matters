package com.squad.musicmatters.glance.layout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartService
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.media3.common.util.UnstableApi
import com.squad.musicmatters.core.media.media.MusicService
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.glance.R
import com.squad.musicmatters.glance.layout.PlayerControlsToolBarLayoutDimens.itemsSpacing

@androidx.annotation.OptIn( UnstableApi::class )
@Composable
internal fun PlayerControlsToolBarLayout(
    isPlaying: Boolean,
    shuffle: Boolean,
    loopMode: LoopMode,
) {

    val context = LocalContext.current

    val minimalControlsButtons = listOf(
        PlayerControlButton(
            iconRes = R.drawable.round_skip_previous_24,
            contentDescription = "skip to previous button",
            onClick = context.startMusicService( MusicService.ACTION_SKIP_TO_PREVIOUS )
        ),
        PlayerControlButton(
            iconRes = if ( isPlaying ) {
                R.drawable.round_pause_24
            } else {
                R.drawable.round_play_arrow_24
            },
            contentDescription = "skip to previous button",
            onClick = context.startMusicService( intentAction = MusicService.ACTION_PLAY_PAUSE )
        ),
        PlayerControlButton(
            iconRes = R.drawable.round_skip_next_24,
            contentDescription = "skip to previous button",
            onClick = context.startMusicService( MusicService.ACTION_SKIP_TO_NEXT )
        )
    )

    val expandedControlsButtons =
        listOf(
            PlayerControlButton(
                iconRes = if ( loopMode == LoopMode.Song ) {
                    R.drawable.round_repeat_one_24
                } else {
                    R.drawable.round_repeat_24
                },
                iconSize = 24.dp,
                isActive = loopMode != LoopMode.None,
                contentDescription = "skip to previous button",
                onClick = context.startMusicService(
                    intentAction = MusicService.ACTION_LOOP_MODE,
                    intentExtras = Pair( MusicService.LOOP_MODE_KEY, loopMode.name )
                ),
            )
        ) + minimalControlsButtons +
                listOf(
                    PlayerControlButton(
                        iconRes = R.drawable.round_shuffle_24,
                        iconSize = 24.dp,
                        isActive = shuffle,
                        contentDescription = "skip to previous button",
                        onClick = actionStartService(
                            Intent(
                                context,
                                MusicService::class.java
                            ).apply {
                                action = MusicService.ACTION_SHUFFLE
                                putExtra(
                                    MusicService.SHUFFLE_MODE_KEY,
                                    shuffle.not()
                                )
                            }
                        )
                    )
                )

    when ( PlayerControlsToolBarLayoutSize.fromLocalSize() ) {
        PlayerControlsToolBarLayoutSize.ExpandedControlsRow -> {
            SpacedRow(
                items = expandedControlsButtons.map { { FluidContentIconButton( it ) } },
                spacing = itemsSpacing,
                modifier = GlanceModifier
                    .fillMaxSize()
            )
        }
        PlayerControlsToolBarLayoutSize.MinimalControlsRow -> {
            SpacedRow(
                items = minimalControlsButtons.map { { FluidContentIconButton( it ) } },
                spacing = itemsSpacing,
                modifier = GlanceModifier
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun FluidContentIconButton(
    button: PlayerControlButton,
    filled: Boolean = false,
) {
    Column (
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RectangularIconButton(
            iconImageProvider = ImageProvider( button.iconRes ),
            contentDescription = button.contentDescription,
            iconSize = button.iconSize,
            roundedCornerShape = RoundedCornerShape.MEDIUM,
            backgroundColor = if ( filled ) {
                GlanceTheme.colors.secondaryContainer
            } else {
                ColorProvider( Color.Transparent, Color.Transparent )
            },
            contentColor = GlanceTheme.colors.onSecondaryContainer,
            onClick = button.onClick,
            isActive = button.isActive,
            modifier = GlanceModifier.fillMaxSize(),
        )
    }
}

@Composable
fun SpacedRow(
    items: List<@Composable () -> Unit>,
    spacing: Dp,
    modifier: GlanceModifier = GlanceModifier.fillMaxWidth(),
) {
    val padding = spacing / 2 // split spacing between siblings

    Column( modifier = modifier ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .defaultWeight()
        ) {
            items.forEachIndexed { index, item ->
                val paddingModifier = when ( index ) {
                    // Right padding only
                    0 -> GlanceModifier.padding( end = padding )

                    // Left padding only
                    items.lastIndex -> GlanceModifier.padding( start = padding )

                    // Both left and right padding
                    else -> GlanceModifier.padding( start = padding, end = padding )
                }

                Box(
                    modifier = paddingModifier
                        .fillMaxHeight()
                        .defaultWeight()
                ) {
                    item()
                }
            }
        }
    }
}

data class PlayerControlButton(
    @param:DrawableRes val iconRes: Int,
    val contentDescription: String,
    val onClick: Action,
    val iconSize: Dp = 32.dp,
    val isActive: Boolean = false,
)

private enum class PlayerControlsToolBarLayoutSize {

    ExpandedControlsRow,
    MinimalControlsRow;

    companion object {

        @Composable
        fun fromLocalSize(): PlayerControlsToolBarLayoutSize {
            val size = LocalSize.current
            val width = size.width

            return if ( width > 300.dp ) {
                ExpandedControlsRow
            } else {
                MinimalControlsRow
            }
        }
    }

}

private object PlayerControlsToolBarLayoutDimens {
    /** Minimum size needed for buttons / clickable areas for accessibility. */
    val minButtonSize = 48.dp

    /** Padding around the content within the widget. */
    val widgetPadding = 12.dp

    /** Spacing between buttons in all layouts. */
    val itemsSpacing = 8.dp
}


@androidx.annotation.OptIn( UnstableApi::class )
internal fun Context.startMusicService(
    intentAction: String,
    intentExtras: Pair<String, String>? = null,
) = actionStartService(
    Intent(
        this,
        MusicService::class.java
    ).apply {
        action = intentAction
        intentExtras?.let {
            putExtra( it.first, it.second )
        }
    }
)

/**
 * Previews for various breakpoints for this layout.
 */
@OptIn( ExperimentalGlancePreviewApi::class )
@Preview( widthDp = 301, heightDp = 172 )
@Preview( widthDp = 256, heightDp = 172 )
@Composable
private fun PlayerControlsToolBarLayoutPreview() {
    PlayerControlsToolBarLayout(
        isPlaying = true,
        loopMode = LoopMode.Song,
        shuffle = false
    )
}