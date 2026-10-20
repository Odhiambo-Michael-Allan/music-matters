package com.squad.musicmatters.feature.player.components

import android.graphics.Bitmap
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import androidx.core.graphics.scale
import com.squad.musicmatters.core.designsystem.theme.LocalColorSchemeMode
import com.squad.musicmatters.core.designsystem.theme.generateBrandColorScheme
import com.squad.musicmatters.core.designsystem.theme.isLight

/**
 * Extracts an artwork-seeded Material color scheme for the player.
 * Falls back to the ambient [MaterialTheme.colorScheme] until extraction completes.
 *
 * Uses Coil [coil.ImageLoader.execute] (not an undisplayed AsyncImagePainter) so palette
 * extraction still runs after process death when the memory cache is cold. Disk cache
 * is enabled so a kill-from-recents reopen can reseed from disk without waiting on
 * network — matching how Explore extracts card accents.
 */
@Composable
fun rememberPlayerColorScheme( imageUrl: String? ): ColorScheme {
    val context = LocalContext.current
    val ambientScheme = MaterialTheme.colorScheme
    val lightThemeApplied = LocalColorSchemeMode.current.isLight()

    var extracted by remember( imageUrl ) {
        mutableStateOf<ColorScheme?>( null )
    }

    LaunchedEffect( imageUrl ) {
        if ( imageUrl.isNullOrBlank() ) {
            extracted = null
            return@LaunchedEffect
        }
        extracted =
            runCatching {
                val request =
                    ImageRequest.Builder( context )
                        .data( imageUrl )
//                        .size( Size( 100, 100 ) )
                        .allowHardware( false ) // Required for Palette pixel reads
                        .memoryCachePolicy( CachePolicy.ENABLED )
                        .diskCachePolicy( CachePolicy.ENABLED )
                        .build()
                val result =
                    context.imageLoader.execute( request ) as? SuccessResult
                        ?: return@runCatching null
                val bitmap = result.image.toBitmap()
                val seedColor = extractSeedColor( bitmap )
                generateBrandColorScheme( seedColor = seedColor, isDark = !lightThemeApplied )
            }.getOrNull()
    }

    return extracted ?: ambientScheme
}

/**
 * Extracts a seed color from a bitmap using Palette API.
 */
suspend fun extractSeedColor( bitmap: Bitmap ): Color = withContext( Dispatchers.Default ) {
    val scaledBitmap = if (bitmap.width > 100 || bitmap.height > 100) {
        val scale = 100f / max(bitmap.width, bitmap.height)
        val scaledWidth = (bitmap.width * scale).toInt().coerceAtLeast(1)
        val scaledHeight = (bitmap.height * scale).toInt().coerceAtLeast(1)
        bitmap.scale( scaledWidth, scaledHeight )
    } else {
        bitmap
    }

    val palette = Palette.Builder(scaledBitmap)
        .maximumColorCount(16)
        .generate()

    val color = palette.vibrantSwatch?.rgb?.let { Color(it) }
        ?: palette.mutedSwatch?.rgb?.let { Color(it) }
        ?: palette.dominantSwatch?.rgb?.let { Color(it) }
        ?: Color(0xFF6750A4) // Default purple

    if (scaledBitmap != bitmap) {
        scaledBitmap.recycle()
    }

    color
}

/**
 * Immersive full-player background: a vertical artwork-tinted gradient with a soft
 * radial glow behind the hero area. Respects AMOLED/surface styles because the
 * scheme itself is produced by [generateBrandColorScheme].
 */
fun Modifier.playerCanvas(scheme: ColorScheme): Modifier = drawBehind {
    val top = scheme.primaryContainer.copy(alpha = 0.65f).compositeOver(scheme.surface)
    val mid = scheme.primaryContainer.copy(alpha = 0.35f).compositeOver(scheme.surface)
    val bottom = scheme.primaryContainer.copy(alpha = 0.12f).compositeOver(scheme.surface)

    drawRect(
        brush = Brush.verticalGradient(
            0f to top,
            0.55f to mid,
            1f to bottom
        )
    )

    // Soft glow behind the hero (upper third of the canvas)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                scheme.primary.copy(alpha = 0.20f),
                Color.Transparent
            ),
            center = Offset(size.width / 2f, size.height * 0.28f),
            radius = size.width * 0.85f
        ),
        center = Offset(size.width / 2f, size.height * 0.28f),
        radius = size.width * 0.85f
    )
}

/** Flat sheet color used while collapsed (mini player) — matches the classic pill look. */
fun miniPlayerSheetColor(scheme: ColorScheme): Color = scheme.primaryContainer