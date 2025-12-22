package com.squad.musicmatters.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

object ThemeColorSchemes {
    private val LightBackgroundColor = ThemeColors.Neutral50
    private val LightSurfaceColor = ThemeColors.Neutral100
    private val LightSurfaceVariantColor = ThemeColors.Neutral200
    private val DarkBackgroundColor = ThemeColors.Neutral900
    private val DarkSurfaceColor = ThemeColors.Neutral900
    private val DarkSurfaceVariantColor = ThemeColors.Neutral800
    private val LightContrastColor = Color.White
    private val BlackContrastColor = Color.Black

    private const val BackgroundBlendRatio = 0.03f
    private const val SurfaceBlendRatio = 0.02f
    private const val SurfaceVariantBlendRatio = 0.01f
    private const val BlackSurfaceBlendRatio = 0.05f
    private const val BlackSurfaceVariantBlendRatio = 0.06f
    private const val DarkOnPrimaryLightness = -0.3f
    private const val DarkOnSecondaryLightness = -0.4f
    private const val DarkOnTertiaryLightness = -0.5f
    private const val LightOnBackgroundLightness = -0.5f
    private const val LightOnSurfaceLightness = -0.5f
    private const val LightOnSurfaceVariantLightness = -0.45f
    private const val DarkToBlackBlendRatio = 0.4f

    fun createLightColorScheme( primaryColor: Color ) = lightColorScheme(
        primary = primaryColor,
        secondary = primaryColor,
        tertiary = primaryColor,
        background = blendColors(
            LightBackgroundColor,
            primaryColor,
            BackgroundBlendRatio
        ),
        surface = blendColors( LightSurfaceColor, primaryColor, SurfaceBlendRatio ),
        onPrimary = LightContrastColor,
        onSecondary = LightContrastColor,
        onTertiary = LightContrastColor,
        onBackground = adjustLightness( primaryColor, LightOnBackgroundLightness ),
        onSurface = adjustLightness( primaryColor, LightOnSurfaceLightness ),
        onPrimaryContainer = LightContrastColor,
        surfaceVariant = blendColors( LightSurfaceVariantColor, primaryColor, SurfaceBlendRatio ),
        surfaceContainer = blendColors( LightSurfaceColor, primaryColor, 0.1f ),
        secondaryContainer = blendColors( LightSurfaceColor, primaryColor, 0.3f ),
    )

    fun createDarkColorScheme( primaryColor: Color) = darkColorScheme(
        primary = primaryColor,
        secondary = primaryColor,
        tertiary = primaryColor,
        background = blendColors(
            DarkBackgroundColor,
            primaryColor,
            BackgroundBlendRatio
        ),
        surface = blendColors( DarkSurfaceColor, primaryColor, SurfaceBlendRatio ),
        onPrimary = adjustLightness( primaryColor, DarkOnPrimaryLightness ),
        onSecondary = adjustLightness( primaryColor, DarkOnSecondaryLightness ),
        onTertiary = adjustLightness( primaryColor, DarkOnTertiaryLightness ),
        onBackground = LightContrastColor,
        onSurface = LightContrastColor,
        onPrimaryContainer = LightContrastColor,
        surfaceVariant = blendColors(
            DarkSurfaceVariantColor,
            primaryColor,
            SurfaceVariantBlendRatio
        ),
        surfaceContainer = blendColors( DarkSurfaceColor, primaryColor, 0.1f ),
        secondaryContainer = blendColors( DarkSurfaceColor, primaryColor, 0.3f ),
    )

    fun createBlackColorScheme( primaryColor: Color ) = darkColorScheme(
        primary = primaryColor,
        onPrimary = adjustLightness( primaryColor, DarkOnPrimaryLightness ),
        primaryContainer = primaryColor,
        onPrimaryContainer = LightContrastColor,
        secondary = primaryColor,
        onSecondary = adjustLightness( primaryColor, DarkOnSecondaryLightness ),
        secondaryContainer = primaryColor,
        onSecondaryContainer = LightContrastColor,
        tertiary = primaryColor,
        onTertiary = adjustLightness( primaryColor, DarkOnTertiaryLightness ),
        tertiaryContainer = primaryColor,
        onTertiaryContainer = LightContrastColor,
        background = BlackContrastColor,
        onBackground = LightContrastColor,
        surface = blendColors( BlackContrastColor, primaryColor, BlackSurfaceBlendRatio ),
        onSurface = LightContrastColor,
        surfaceVariant = blendColors(
            BlackContrastColor,
            primaryColor,
            BlackSurfaceVariantBlendRatio
        ),
        onSurfaceVariant = LightContrastColor,
    )

    fun toBlackColorScheme( colorScheme: ColorScheme ) = colorScheme.copy(
        primaryContainer = convertDarkToBlack( colorScheme.primaryContainer ),
        secondaryContainer = convertDarkToBlack( colorScheme.secondaryContainer ),
        tertiaryContainer = convertDarkToBlack( colorScheme.tertiaryContainer ),
        background = BlackContrastColor,
        surface = convertDarkToBlack( colorScheme.surface ),
        surfaceVariant = convertDarkToBlack( colorScheme.surfaceVariant ),
        surfaceTint = convertDarkToBlack( colorScheme.surfaceTint ),
    )

    private fun convertDarkToBlack( color: Color ): Color {
        val argb = ColorUtils.blendARGB(
            BlackContrastColor.toArgb(),
            color.toArgb(),
            DarkToBlackBlendRatio,
        )
        return Color( argb )
    }

    private fun blendColors( color1: Color, color2: Color, ratio: Float ) =
        Color( ColorUtils.blendARGB( color1.toArgb(), color2.toArgb(), ratio ) )

    private fun adjustLightness( color: Color, threshold: Float ): Color {
        val hsl = convertColorToHSL(color)
        hsl[ 2 ] = hsl[ 2 ] + threshold
        return convertHSLToColor( hsl )
    }

    private fun convertColorToHSL( color: Color ): FloatArray {
        val out = FloatArray( 3 )
        ColorUtils.colorToHSL( color.toArgb(), out )
        return out
    }

    private fun convertHSLToColor( hsl: FloatArray ) =
        Color( ColorUtils.HSLToColor( hsl ) )
}