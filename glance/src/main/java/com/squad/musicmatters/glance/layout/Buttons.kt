package com.squad.musicmatters.glance.layout

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.semantics.contentDescription
import androidx.glance.semantics.semantics
import androidx.glance.unit.ColorProvider
import com.squad.musicmatters.glance.R


@Composable
internal fun RectangularIconButton(
    iconImageProvider: ImageProvider,
    onClick: Action,
    roundedCornerShape: RoundedCornerShape,
    contentDescription: String,
    iconSize: Dp,
    modifier: GlanceModifier,
    isActive: Boolean = false,
    backgroundColor: ColorProvider = GlanceTheme.colors.primary,
    contentColor: ColorProvider = GlanceTheme.colors.onPrimary,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background( backgroundColor )
            .cornerRadius( roundedCornerShape.cornerRadius )
            .semantics { this.contentDescription = contentDescription }
            .clickable( onClick )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                provider = iconImageProvider,
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    if ( isActive ) GlanceTheme.colors.primary
                    else contentColor
                ),
                modifier = GlanceModifier.size( iconSize )
            )
            if ( isActive ) {
                Spacer( modifier = GlanceModifier.height( 1.dp ) )
                Box(
                    modifier = GlanceModifier
                        .size( 4.dp )
                        .background( GlanceTheme.colors.primary )
                        .cornerRadius( 50.dp )
                ) {}
            }
        }
    }
}

@Composable
internal fun PillShapedButton(
    iconImageProvider: ImageProvider,
    iconSize: Dp,
    backgroundColor: ColorProvider,
    contentColor: ColorProvider,
    contentDescription: String,
    onClick: Action,
    modifier: GlanceModifier,
) {
    Box( // A clickable transparent outer container
        contentAlignment = Alignment.Center,
        modifier = modifier
            .semantics { this.contentDescription = contentDescription }
            .height( 48.dp )
            .clickable( onClick )
    ) {
        Box( // A filled background with smaller height
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier
                .width( 52.dp )
                .height( 32.dp )
                .background( backgroundColor )
                .cornerRadius( RoundedCornerShape.FULL.cornerRadius )
        ) { // The icon.
            Image(
                provider = iconImageProvider,
                contentDescription = null,
                colorFilter = ColorFilter.tint( contentColor ),
                modifier = GlanceModifier.size( iconSize )
            )
        }
    }
}

/**
 * Defines the roundness of a shape inline with the tokens used in M3
 * https://m3.material.io/styles/shape/shape-scale-tokens
 */
enum class RoundedCornerShape( val cornerRadius: Dp ) {
    FULL( 100.dp ),
    MEDIUM( 16.dp ),
}

@OptIn( ExperimentalGlancePreviewApi::class )
@Preview
@Composable
private fun ButtonsPreview() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.padding( 12.dp ),
    ) {
        PillShapedButton(
            iconImageProvider = ImageProvider( resId = R.drawable.round_play_arrow_24 ),
            contentDescription = "",
            iconSize = 24.dp,
            backgroundColor = GlanceTheme.colors.tertiary,
            contentColor = GlanceTheme.colors.onTertiary,
            onClick = actionRunCallback<NoOpAction>(),
            modifier = GlanceModifier
        )
        Spacer( modifier = GlanceModifier.width( 8.dp ) )
        RectangularIconButton(
            iconImageProvider = ImageProvider( resId = R.drawable.round_play_arrow_24 ),
            contentDescription = "",
            iconSize = 24.dp,
            roundedCornerShape = RoundedCornerShape.MEDIUM,
            onClick = actionRunCallback<NoOpAction>(),
            modifier = GlanceModifier
        )
    }
}

internal class NoOpAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {}
}