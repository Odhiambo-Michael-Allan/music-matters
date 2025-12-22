package com.squad.musicmatters.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * A class to model the background color and tonal elevation values for MusicMatters
 */
@Immutable
data class TintTheme(
    val iconTint: Color = Color.Unspecified
)

/**
 * A composition local for [TintTheme]
 */
val LocalTintTheme = staticCompositionLocalOf { TintTheme() }