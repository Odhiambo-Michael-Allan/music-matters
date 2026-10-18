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

/**
 * Generates a complete Material 3 color scheme algorithmically from a single semantic seed color.
 * Accepts a surfaceStyle to control background/surface lightness levels.
 */
fun generateBrandColorScheme(
    seedColor: Color,
    isDark: Boolean,
    surfaceStyle: String = SurfaceStyles.STANDARD
): ColorScheme {
    val hsl = FloatArray(3)
    ColorUtils.RGBToHSL(
        (seedColor.red * 255).toInt(),
        (seedColor.green * 255).toInt(),
        (seedColor.blue * 255).toInt(),
        hsl
    )

    fun hslToColor(
        h: Float,
        s: Float,
        l: Float
    ): Color = Color(
        ColorUtils.HSLToColor(
            floatArrayOf( h, s.coerceIn( 0f, 1f ), l.coerceIn( 0f, 1f ) )
        )
    )

    val hue = hsl[0]
    val baseSat = hsl[1].coerceIn(0.3f, 0.8f)

    // Adjust saturation based on surface style
    val sat = if ( surfaceStyle == SurfaceStyles.HIGH_CONTRAST ) {
        ( baseSat * if ( isDark ) 1.3f else 1.2f ).coerceIn( 0.3f, 0.95f )
    } else {
        baseSat
    }

    val isClassic = surfaceStyle == SurfaceStyles.CLASSIC_DYNAMIC ||
            surfaceStyle == SurfaceStyles.CLASSIC_DARK ||
            surfaceStyle == SurfaceStyles.CLASSIC_LIGHT
    val contentSat = if ( isClassic ) 0f else sat

    // Surface lightness values vary by surface style
    val surfaceLevels = getSurfaceLevels( isDark, surfaceStyle, hue, sat )

    return if ( isDark ) {
        darkColorScheme(
            primary = hslToColor(hue, sat, 0.7f),
            onPrimary = hslToColor(hue, sat * 0.3f, 0.15f),
            primaryContainer = hslToColor(hue, sat * 0.5f, 0.25f),
            onPrimaryContainer = hslToColor(hue, sat * 0.3f, 0.9f),
            secondary = hslToColor(hue, sat * 0.4f, 0.7f),
            onSecondary = hslToColor(hue, sat * 0.3f, 0.15f),
            secondaryContainer = hslToColor(hue, sat * 0.4f, 0.25f),
            onSecondaryContainer = hslToColor(hue, sat * 0.3f, 0.9f),
            tertiary = hslToColor(hue + 30f, sat * 0.5f, 0.7f),
            onTertiary = hslToColor(hue + 30f, sat * 0.3f, 0.15f),
            tertiaryContainer = hslToColor(hue + 30f, sat * 0.5f, 0.25f),
            onTertiaryContainer = hslToColor(hue + 30f, sat * 0.3f, 0.9f),
            background = surfaceLevels.background,
            onBackground = hslToColor(hue, contentSat * 0.1f, 0.90f),
            surface = surfaceLevels.surface,
            onSurface = hslToColor(hue, contentSat * 0.1f, 0.90f),
            surfaceVariant = hslToColor(hue, contentSat * 0.3f, 0.20f),
            onSurfaceVariant = hslToColor(hue, contentSat * 0.2f, 0.75f),
            outline = hslToColor(
                hue,
                contentSat * 0.25f,
                if ( surfaceStyle == SurfaceStyles.HIGH_CONTRAST ) 0.70f else 0.60f ),
            outlineVariant = hslToColor(hue, contentSat * 0.2f, 0.30f),
            surfaceContainerLowest = surfaceLevels.containerLowest,
            surfaceContainerLow = surfaceLevels.containerLow,
            surfaceContainer = surfaceLevels.container,
            surfaceContainerHigh = surfaceLevels.containerHigh,
            surfaceContainerHighest = surfaceLevels.containerHighest
        )
    } else {
        lightColorScheme(
            primary = hslToColor(hue, sat, 0.4f),
            onPrimary = hslToColor(hue, sat * 0.2f, 0.95f),
            primaryContainer = hslToColor(hue, sat * 0.6f, 0.9f),
            onPrimaryContainer = hslToColor(hue, sat * 0.5f, 0.15f),
            secondary = hslToColor(hue, sat * 0.4f, 0.4f),
            onSecondary = hslToColor(hue, sat * 0.2f, 0.95f),
            secondaryContainer = hslToColor(hue, sat * 0.4f, 0.9f),
            onSecondaryContainer = hslToColor(hue, sat * 0.4f, 0.15f),
            tertiary = hslToColor(hue + 30f, sat * 0.5f, 0.4f),
            onTertiary = hslToColor(hue + 30f, sat * 0.2f, 0.95f),
            tertiaryContainer = hslToColor(hue + 30f, sat * 0.5f, 0.9f),
            onTertiaryContainer = hslToColor(hue + 30f, sat * 0.4f, 0.15f),
            background = surfaceLevels.background,
            onBackground = hslToColor(hue, contentSat * 0.1f, 0.10f),
            surface = surfaceLevels.surface,
            onSurface = hslToColor(hue, contentSat * 0.1f, 0.10f),
            surfaceVariant = hslToColor(hue, contentSat * 0.3f, 0.90f),
            onSurfaceVariant = hslToColor(hue, contentSat * 0.2f, 0.30f),
            outline = hslToColor(
                hue,
                contentSat * 0.25f,
                if ( surfaceStyle == SurfaceStyles.HIGH_CONTRAST ) 0.35f else 0.45f ),
            outlineVariant = hslToColor(hue, contentSat * 0.25f, 0.80f),
            surfaceContainerLowest = surfaceLevels.containerLowest,
            surfaceContainerLow = surfaceLevels.containerLow,
            surfaceContainer = surfaceLevels.container,
            surfaceContainerHigh = surfaceLevels.containerHigh,
            surfaceContainerHighest = surfaceLevels.containerHighest
        )
    }
}

/**
 * Surface lightness levels for each surface style mode.
 */
private data class SurfaceLevels(
    val background: Color,
    val surface: Color,
    val containerLowest: Color,
    val containerLow: Color,
    val container: Color,
    val containerHigh: Color,
    val containerHighest: Color
)

private fun getSurfaceLevels(isDark: Boolean, surfaceStyle: String, hue: Float, sat: Float): SurfaceLevels {
    fun hsl(h: Float, s: Float, l: Float) = Color(
        androidx.core.graphics.ColorUtils.HSLToColor(floatArrayOf(h, s.coerceIn(0f, 1f), l.coerceIn(0f, 1f)))
    )

    val style = if (surfaceStyle == SurfaceStyles.DYNAMIC_OLED_WHITE) {
        if (isDark) SurfaceStyles.AMOLED else SurfaceStyles.PURE_WHITE
    } else {
        surfaceStyle
    }

    return when {
        // AMOLED: Pure black background, near-black containers
        style == SurfaceStyles.AMOLED && isDark -> SurfaceLevels(
            background = Color.Black,
            surface = Color.Black,
            containerLowest = Color.Black,
            containerLow = hsl(hue, sat * 0.05f, 0.02f),
            container = hsl(hue, sat * 0.08f, 0.04f),
            containerHigh = hsl(hue, sat * 0.10f, 0.07f),
            containerHighest = hsl(hue, sat * 0.12f, 0.10f)
        )
        // AMOLED in light mode: behave as classic dynamic (default)
        style == SurfaceStyles.AMOLED && !isDark -> getSurfaceLevels(isDark, SurfaceStyles.CLASSIC_DYNAMIC, hue, sat)

        // Pure White: #FFFFFF background, near-white containers
        style == SurfaceStyles.PURE_WHITE && !isDark -> SurfaceLevels(
            background = Color.White,
            surface = Color.White,
            containerLowest = Color.White,
            containerLow = hsl(hue, sat * 0.05f, 0.98f),
            container = hsl(hue, sat * 0.08f, 0.96f),
            containerHigh = hsl(hue, sat * 0.10f, 0.93f),
            containerHighest = hsl(hue, sat * 0.12f, 0.89f)
        )
        // Pure White in dark mode: behave as classic dynamic (default)
        style == SurfaceStyles.PURE_WHITE && isDark -> getSurfaceLevels(isDark, SurfaceStyles.CLASSIC_DYNAMIC, hue, sat)

        // High Contrast Dark: deeper blacks, wider spread
        style == SurfaceStyles.HIGH_CONTRAST && isDark -> SurfaceLevels(
            background = hsl(hue, sat * 0.05f, 0.03f),
            surface = hsl(hue, sat * 0.05f, 0.03f),
            containerLowest = hsl(hue, sat * 0.03f, 0.01f),
            containerLow = hsl(hue, sat * 0.08f, 0.05f),
            container = hsl(hue, sat * 0.10f, 0.09f),
            containerHigh = hsl(hue, sat * 0.12f, 0.14f),
            containerHighest = hsl(hue, sat * 0.15f, 0.22f)
        )
        // High Contrast Light: pure white, wider spread
        style == SurfaceStyles.HIGH_CONTRAST && !isDark -> SurfaceLevels(
            background = Color.White,
            surface = Color.White,
            containerLowest = Color.White,
            containerLow = hsl(hue, sat * 0.08f, 0.97f),
            container = hsl(hue, sat * 0.12f, 0.93f),
            containerHigh = hsl(hue, sat * 0.15f, 0.88f),
            containerHighest = hsl(hue, sat * 0.18f, 0.82f)
        )

        // Classic styles: neutral grays (no tinting at all)
        style == SurfaceStyles.CLASSIC_DYNAMIC || style == SurfaceStyles.CLASSIC_DARK || style == SurfaceStyles.CLASSIC_LIGHT -> {
            if (isDark) {
                SurfaceLevels(
                    background = Color(0xFF111316),
                    surface = Color(0xFF111316),
                    containerLowest = Color(0xFF111316),
                    containerLow = Color(0xFF181B20),
                    container = Color(0xFF181B20),
                    containerHigh = Color(0xFF21252B),
                    containerHighest = Color(0xFF21252B)
                )
            } else {
                SurfaceLevels(
                    background = Color(0xFFF0EBE0),
                    surface = Color(0xFFF0EBE0),
                    containerLowest = Color(0xFFFFFFFF),
                    containerLow = Color(0xFFFBF8F1),
                    container = Color(0xFFFBF8F1),
                    containerHigh = Color(0xFFFFFFFF),
                    containerHighest = Color(0xFFFFFFFF)
                )
            }
        }

        // Standard Dark
        isDark -> SurfaceLevels(
            background = hsl(hue, sat * 0.15f, 0.10f),
            surface = hsl(hue, sat * 0.15f, 0.10f),
            containerLowest = hsl(hue, sat * 0.15f, 0.05f),
            containerLow = hsl(hue, sat * 0.15f, 0.08f),
            container = hsl(hue, sat * 0.15f, 0.12f),
            containerHigh = hsl(hue, sat * 0.15f, 0.15f),
            containerHighest = hsl(hue, sat * 0.15f, 0.20f)
        )
        // Standard Light
        else -> SurfaceLevels(
            background = hsl(hue, sat * 0.1f, 0.98f),
            surface = hsl(hue, sat * 0.1f, 0.98f),
            containerLowest = hsl(hue, sat * 0.1f, 1.00f),
            containerLow = hsl(hue, sat * 0.1f, 0.96f),
            container = hsl(hue, sat * 0.15f, 0.93f),
            containerHigh = hsl(hue, sat * 0.15f, 0.90f),
            containerHighest = hsl(hue, sat * 0.15f, 0.86f)
        )
    }
}

/**
 * Surface style modes that control background/surface lightness.
 */
object SurfaceStyles {
    const val STANDARD = "standard"
    const val AMOLED = "amoled"
    const val PURE_WHITE = "purewhite"
    const val HIGH_CONTRAST = "highcontrast"
    const val DYNAMIC_OLED_WHITE = "dynamic_oled_white"
    const val CLASSIC_DYNAMIC = "classic_dynamic"
    const val CLASSIC_DARK = "classic_dark"
    const val CLASSIC_LIGHT = "classic_light"

    /** Display labels for the settings UI. */
    val entries = listOf(
        Entry(CLASSIC_DYNAMIC, "boxlore classic", "Gentle gray when dark, warm cream when light."),
        Entry(STANDARD, "Material You Soft", "Colored surfaces from your wallpaper or accent."),
        Entry(CLASSIC_DARK, "Blackish", "Soft dark gray · stays dark."),
        Entry(CLASSIC_LIGHT, "Whitish", "Soft cream · stays light."),
        Entry(DYNAMIC_OLED_WHITE, "Pure", "True black when dark, pure white when light."),
        Entry(AMOLED, "Pitch black", "True black · stays dark · good for OLED."),
        Entry(PURE_WHITE, "Pure white", "Bright white · stays light."),
        Entry(HIGH_CONTRAST, "High Contrast", "Enhanced readability · bolder colors")
    )

    data class Entry(val key: String, val label: String, val subtitle: String)
}