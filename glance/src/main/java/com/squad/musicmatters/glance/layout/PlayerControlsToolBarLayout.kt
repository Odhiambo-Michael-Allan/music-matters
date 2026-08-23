package com.squad.musicmatters.glance.layout

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import com.squad.musicmatters.glance.R
import com.squad.musicmatters.glance.layout.PlayerControlsToolBarLayoutDimens.itemsSpacing

@Composable
internal fun PlayerControlsToolBarLayout() {

    val minimalControlsButtons = listOf(
        PlayerControlButton(
            iconRes = R.drawable.round_skip_previous_24,
            contentDescription = "skip to previous button",
            onClick = actionRunCallback<NoOpAction>()
        ),
        PlayerControlButton(
            iconRes = R.drawable.round_play_arrow_24,
            contentDescription = "skip to previous button",
            onClick = actionRunCallback<NoOpAction>()
        ),
        PlayerControlButton(
            iconRes = R.drawable.round_skip_next_24,
            contentDescription = "skip to previous button",
            onClick = actionRunCallback<NoOpAction>()
        )
    )

    val expandedControlsButtons =
        listOf(
            PlayerControlButton(
                iconRes = R.drawable.round_repeat_24,
                iconSize = 24.dp,
                contentDescription = "skip to previous button",
                onClick = actionRunCallback<NoOpAction>(),
            )
        ) + minimalControlsButtons +
                listOf(
                    PlayerControlButton(
                        iconRes = R.drawable.round_shuffle_24,
                        iconSize = 24.dp,
                        contentDescription = "skip to previous button",
                        onClick = actionRunCallback<NoOpAction>()
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
    filled: Boolean = false
) {
    RectangularIconButton(
        iconImageProvider = ImageProvider(button.iconRes),
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
        modifier = GlanceModifier.fillMaxSize()
    )
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

/**
 * Previews for various breakpoints for this layout.
 */
@OptIn( ExperimentalGlancePreviewApi::class )
@Preview( widthDp = 301, heightDp = 72 )
@Preview( widthDp = 256, heightDp = 72 )
@Composable
private fun PlayerControlsToolBarLayoutPreview() {
    PlayerControlsToolBarLayout()
}